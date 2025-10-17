package com.example.green.domain.dashboard.rankingmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.challenge.QBaseChallengeParticipation;
import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.dto.WeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.global.error.exception.BusinessException;
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
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public WeeklyRankingResponse calculateWeeklyRanking(LocalDate weekStart, int topN, Long currentMemberId) {
		QMember member = QMember.member;
		QPointTransaction point = QPointTransaction.pointTransaction;
		QBaseChallengeParticipation challenge = QBaseChallengeParticipation.baseChallengeParticipation;

		LocalDateTime startDateTime = weekStart.atStartOfDay();
		LocalDateTime endDateTime = weekStart.plusDays(6).atTime(23, 59, 59);

		List<Tuple> topMembers = queryFactory
			.select(
				member.id,
				member.name,
				point.pointAmount.amount.sum(),
				challenge.certCount.sum().coalesce(0)
			)
			.from(member)
			.leftJoin(point)
			.on(point.memberId.eq(member.id)
				.and(point.type.eq(TransactionType.EARN))
				.and(point.createdDate.between(startDateTime, endDateTime)))
			.leftJoin(challenge)
			.on(challenge.memberId.eq(member.id)
				.and(challenge.participatedAt.between(startDateTime, endDateTime)))
			.groupBy(member.id, member.name)
			.orderBy(
				point.pointAmount.amount.sum().desc(),
				challenge.certCount.sum().desc()
			)
			.limit(topN)
			.fetch();

		List<TopMemberPointResponseDto> topMembersDto = topMembers.stream()
			.map(tuple -> new TopMemberPointResponseDto(
				tuple.get(member.name),
				tuple.get(point.pointAmount.amount.sum()) != null ? tuple.get(point.pointAmount.amount.sum()) :
					BigDecimal.ZERO,
				tuple.get(challenge.certCount.sum().coalesce(0))
			))
			.toList();

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

		Member currentMember = memberRepository.findById(currentMemberId)
			.orElseThrow(() -> new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND));

		Tuple myTuple = queryFactory
			.select(
				point.pointAmount.amount.sum(),
				challenge.certCount.sum().coalesce(0)
			)
			.from(member)
			.leftJoin(point)
			.on(point.memberId.eq(member.id)
				.and(point.type.eq(TransactionType.EARN))
				.and(point.createdDate.between(startDateTime, endDateTime)))
			.leftJoin(challenge)
			.on(challenge.memberId.eq(member.id)
				.and(challenge.participatedAt.between(startDateTime, endDateTime)))
			.where(member.id.eq(currentMemberId))
			.fetchOne();

		BigDecimal myPoint = myTuple != null && myTuple.get(point.pointAmount.amount.sum()) != null
			? myTuple.get(point.pointAmount.amount.sum()) : BigDecimal.ZERO;

		int myCertCount = myTuple != null ? myTuple.get(challenge.certCount.sum().coalesce(0)) : 0;

		TopMemberPointResponseDto myData = new TopMemberPointResponseDto(
			currentMember.getName(),
			myPoint,
			myCertCount
		);

		return new WeeklyRankingResponse(topMembersDto, myData);
	}
}
