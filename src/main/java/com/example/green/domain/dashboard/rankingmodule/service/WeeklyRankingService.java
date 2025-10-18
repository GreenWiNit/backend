package com.example.green.domain.dashboard.rankingmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.challenge.QBaseChallengeParticipation;
import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.dto.WeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema
@RequiredArgsConstructor
@Service
public class WeeklyRankingService {

	private final JPAQueryFactory queryFactory;
	private final WeeklyRankingRepository weeklyRankingRepository;
	private final MemberRepository memberRepository;

	/**
	 * 이번주 상위 N명 랭킹 계산 및 저장
	 */
	@Transactional
	public void calculateAndSaveWeeklyRanking(LocalDate weekStart, int topN, Long systemUserId) {
		QMember member = QMember.member;
		QPointTransaction point = QPointTransaction.pointTransaction;
		QBaseChallengeParticipation challenge = QBaseChallengeParticipation.baseChallengeParticipation;

		LocalDateTime startDateTime = weekStart.atStartOfDay();
		LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

		if (!weeklyRankingRepository.findByWeekStart(weekStart).isEmpty()) {
			return;
		}

		List<Tuple> topMembers = queryFactory
			.select(member.id, member.name, point.pointAmount.amount.sum(), challenge.certCount.sum().coalesce(0))
			.from(member)
			.leftJoin(point).on(point.memberId.eq(member.id)
				.and(point.type.eq(TransactionType.EARN))
				.and(point.createdDate.between(startDateTime, endDateTime)))
			.leftJoin(challenge).on(challenge.memberId.eq(member.id)
				.and(challenge.participatedAt.between(startDateTime, endDateTime)))
			.groupBy(member.id, member.name)
			.orderBy(point.pointAmount.amount.sum().desc(), challenge.certCount.sum().desc())
			.limit(topN)
			.fetch();

		for (int i = 0; i < topMembers.size(); i++) {
			Tuple tuple = topMembers.get(i);
			WeeklyRanking ranking = WeeklyRanking.builder()
				.memberId(tuple.get(member.id))
				.memberName(tuple.get(member.name))
				.totalEarned(tuple.get(point.pointAmount.amount.sum()))
				.certificationCount(tuple.get(challenge.certCount.sum().coalesce(0)))
				.rank(String.valueOf(i + 1))
				.weekStart(weekStart)
				.build();
			weeklyRankingRepository.save(ranking);
		}
	}

	/**
	 * 저장되어 있는 랭킹 조회
	 */
	@Transactional(readOnly = true)
	public WeeklyRankingResponse viewWeeklyRanking(LocalDate weekStart, Long currentMemberId, int topN) {
		List<WeeklyRanking> topMembers = weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(weekStart, topN);

		TopMemberPointResponseDto myData = topMembers.stream()
			.filter(r -> r.getMemberId().equals(currentMemberId))
			.findFirst()
			.map(r -> new TopMemberPointResponseDto(r.getMemberName(), r.getTotalEarned(), r.getCertificationCount()))
			.orElse(new TopMemberPointResponseDto("Unknown", BigDecimal.ZERO, 0));

		List<TopMemberPointResponseDto> topMembersDto = topMembers.stream()
			.map(r -> new TopMemberPointResponseDto(r.getMemberName(), r.getTotalEarned(), r.getCertificationCount()))
			.toList();

		return new WeeklyRankingResponse(topMembersDto, myData);
	}
}
