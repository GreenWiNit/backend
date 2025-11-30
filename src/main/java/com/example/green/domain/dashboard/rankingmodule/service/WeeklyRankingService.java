package com.example.green.domain.dashboard.rankingmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.exception.WeeklyRankingException;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingExceptionMessage;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WeeklyRankingService {

	private final WeeklyRankingRepository weeklyRankingRepository;
	private final MemberRepository memberRepository;

	//모든 주별 TopN 계산 로직 추가
	@Transactional
	public void updateWeeklyRanks() {

		List<WeeklyRanking> allWeeklyRankings = weeklyRankingRepository.findAllRankings();

		Map<LocalDate, List<WeeklyRanking>> rankingsByWeek = allWeeklyRankings.stream()
			.collect(Collectors.groupingBy(WeeklyRanking::getWeekStart));

		for (Map.Entry<LocalDate, List<WeeklyRanking>> entry : rankingsByWeek.entrySet()) {

			List<WeeklyRanking> weeklyRankings = entry.getValue();

			weeklyRankings.sort(
				Comparator.comparing(WeeklyRanking::getTotalPoint, Comparator.reverseOrder())
					.thenComparing(WeeklyRanking::getCertificationCount, Comparator.reverseOrder())
			);

			for (int i = 0; i < weeklyRankings.size(); i++) {
				WeeklyRanking weeklyRanking = weeklyRankings.get(i);
				int newRank = i + 1;
				if (weeklyRanking.getRank() != newRank) {
					weeklyRanking.setRank(newRank);
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
