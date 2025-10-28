package com.example.green.domain.dashboard.rankingmodule.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.challenge.QBaseChallengeParticipation;
import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.TopMemberPointResponseDto;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.exception.WeeklyRankingException;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingExceptionMessage;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.point.entity.QPointTransaction;
import com.querydsl.core.Tuple;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WeeklyRankingService {

	private final WeeklyRankingRepository weeklyRankingRepository;

	@Transactional
	public void calculateAndSaveWeeklyRanking(LocalDate weekStart, int topN) {

		if (topN <= 0) {
			throw new WeeklyRankingException(WeeklyRankingExceptionMessage.NEGATIVE_NUMBER_NOT_ALLOWED);
		}

		if (!weeklyRankingRepository.findByWeekStart(weekStart).isEmpty()) {
			return;
		}

		List<Tuple> topMembers = weeklyRankingRepository.findTopMembersOfWeek(weekStart, topN);

		for (Tuple tuple : topMembers) {
			boolean hasNull = Arrays.stream(tuple.toArray()).anyMatch(v -> v == null);
			if (hasNull) {
				throw new WeeklyRankingException(WeeklyRankingExceptionMessage.VALIDATION_NULL_OR_BLANK_);
			}
		}

		for (int i = 0; i < topMembers.size(); i++) {
			Tuple rankData = topMembers.get(i);

			WeeklyRanking weeklyRanking = WeeklyRanking.builder()
				.memberId(rankData.get(QMember.member.id))
				.memberName(rankData.get(QMember.member.name))
				.totalPoint(rankData.get(QPointTransaction.pointTransaction.pointAmount.amount.sum()))
				.certificationCount(
					rankData.get(QBaseChallengeParticipation.baseChallengeParticipation.certCount.sum().coalesce(0)))
				.rank(i + 1)
				.weekStart(weekStart)
				.weekEnd(weekStart.plusDays(6))
				.build();

			weeklyRankingRepository.save(weeklyRanking);
		}
	}

	public LoadWeeklyRankingResponse loadWeeklyRanking(LocalDate weekStart, int topN, Long memberId) {

		// 상위 N명 랭킹 엔티티 조회
		List<WeeklyRanking> topMembersFromDb = weeklyRankingRepository.findTopNByWeekStartOrderByRankAsc(weekStart,
			topN);

		// 엔티티 → DTO 변환
		List<TopMemberPointResponseDto> topMembersDto = topMembersFromDb.stream()
			.map(r -> new TopMemberPointResponseDto(
				r.getMemberId(),
				r.getMemberName(),
				r.getTotalPoint(),
				r.getCertificationCount(),
				r.getWeekStart(),
				r.getWeekEnd()
			))
			.toList();

		// 로그인 유저 데이터 조회
		WeeklyRanking myRankingEntity = weeklyRankingRepository.myData(weekStart, memberId)
			.orElseThrow(() -> new WeeklyRankingException(WeeklyRankingExceptionMessage.NOT_FOUND_USER));

		TopMemberPointResponseDto myData = new TopMemberPointResponseDto(
			myRankingEntity.getMemberId(),
			myRankingEntity.getMemberName(),
			myRankingEntity.getTotalPoint(),
			myRankingEntity.getCertificationCount(),
			myRankingEntity.getWeekStart(),
			myRankingEntity.getWeekEnd()
		);

		return new LoadWeeklyRankingResponse(topMembersDto, myData);
	}

}
