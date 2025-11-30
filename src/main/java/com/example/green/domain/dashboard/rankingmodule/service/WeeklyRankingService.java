package com.example.green.domain.dashboard.rankingmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.challenge.QBaseChallengeParticipation;
import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.exception.WeeklyRankingException;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingExceptionMessage;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.QMember;
import com.example.green.domain.member.entity.vo.QProfile;
import com.example.green.domain.member.repository.MemberRepository;
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
	private final MemberRepository memberRepository;

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
				throw new WeeklyRankingException(WeeklyRankingExceptionMessage.VALIDATION_NULL_OR_BLANK);
			}
		}

		List<WeeklyRanking> rankings = new ArrayList<>();

		for (int i = 0; i < topMembers.size(); i++) {
			Tuple rankData = topMembers.get(i);

			WeeklyRanking weeklyRanking = WeeklyRanking.builder()
				.memberId(rankData.get(QMember.member.id))
				.memberName(rankData.get(QMember.member.name))
				.profileImageUrl(rankData.get(QProfile.profile.profileImageUrl))
				.totalPoint(rankData.get(QPointTransaction.pointTransaction.pointAmount.amount.sum()))
				.certificationCount(
					rankData.get(QBaseChallengeParticipation.baseChallengeParticipation.certCount.sum().coalesce(0)))
				.rank(i + 1)
				.weekStart(weekStart)
				.weekEnd(weekStart.plusDays(6))
				.build();

			rankings.add(weeklyRanking);
		}
		weeklyRankingRepository.saveAll(rankings);
	}

	//모든 주별 TopN 계산 로직 추가
	@Transactional
	public void updateWeeklyRanks() {

		List<WeeklyRanking> allWeeklyRankings = weeklyRankingRepository.findAllRankings();

		Map<LocalDate, List<WeeklyRanking>> rankingsByWeek = allWeeklyRankings.stream()
			.collect(Collectors.groupingBy(WeeklyRanking::getWeekStart));

		//변경된 순위만 모으기 위한 리스트
		List<WeeklyRanking> toUpdate = new ArrayList<>();

		for (Map.Entry<LocalDate, List<WeeklyRanking>> entry : rankingsByWeek.entrySet()) {

			List<WeeklyRanking> weeklyRankings = entry.getValue();

			weeklyRankings.sort(Comparator.comparing(WeeklyRanking::getTotalPoint).reversed()
				.thenComparing(WeeklyRanking::getCertificationCount).reversed()
			);

			for (int i = 0; i < weeklyRankings.size(); i++) {
				WeeklyRanking weeklyRanking = weeklyRankings.get(i);
				int newRank = i + 1;
				if (weeklyRanking.getRank() != newRank) {
					weeklyRanking.setRank(newRank);
					toUpdate.add(weeklyRanking);
				}
			}
		}

	}

	public List<TopMemberPointResponse> getAllRankData(LocalDate weekStart, int topN) {

		// 상위 N명 랭킹 엔티티 조회
		List<WeeklyRanking> topMembersFromDb = weeklyRankingRepository.findTopNByWeekStart(weekStart,
			topN);

		if (topMembersFromDb.isEmpty()) {
			return new ArrayList<>();
		}

		// 엔티티 → DTO 변환
		return topMembersFromDb.stream()
			.map(r -> new TopMemberPointResponse(
				r.getMemberId(),
				r.getMemberName(),
				r.getProfileImageUrl(),
				r.getRank(),
				r.getTotalPoint(),
				r.getCertificationCount(),
				r.getWeekStart(),
				r.getWeekEnd()
			))
			.toList();
	}

	public MemberPointResponse getMyData(LocalDate weekStart, Long memberId) {

		Optional<WeeklyRanking> optionalMyRanking =
			weeklyRankingRepository.myData(weekStart, memberId);

		if (optionalMyRanking.isEmpty()) {
			Member member = memberRepository.findById(memberId)
				.orElseThrow(() ->
					new WeeklyRankingException(WeeklyRankingExceptionMessage.NOT_FOUND_USER)
				);

			return new MemberPointResponse(
				member.getId(),
				member.getName(),// 닉네임: member.name
				member.getProfile().getProfileImageUrl(),
				0,
				BigDecimal.ZERO,         // totalPoint = 0 (테스트에서 검증)
				0,                       // certificationCount = 0
				weekStart,
				weekStart.plusDays(6)
			);
		}

		WeeklyRanking myRanking = optionalMyRanking.get();

		return new MemberPointResponse(
			myRanking.getMemberId(),
			myRanking.getMemberName(),
			myRanking.getProfileImageUrl(),
			myRanking.getRank(),
			myRanking.getTotalPoint(),
			myRanking.getCertificationCount(),
			myRanking.getWeekStart(),
			myRanking.getWeekEnd()
		);
	}
}
