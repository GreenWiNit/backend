package com.example.green.domain.dashboard.rankingmodule.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.infra.projections.MemberCertifiedCountProjection;
import com.example.green.domain.dashboard.rankingmodule.dto.response.MemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.dto.response.TopMemberPointResponse;
import com.example.green.domain.dashboard.rankingmodule.entity.WeeklyRanking;
import com.example.green.domain.dashboard.rankingmodule.exception.WeeklyRankingException;
import com.example.green.domain.dashboard.rankingmodule.message.WeeklyRankingExceptionMessage;
import com.example.green.domain.dashboard.rankingmodule.repository.WeeklyRankingRepository;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@Schema
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WeeklyRankingService {

	private final WeeklyRankingRepository weeklyRankingRepository;
	private final MemberRepository memberRepository;
	private final PointTransactionQueryRepository pointTransactionQueryRepository;
	private final ChallengeCertificationRepository challengeCertificationRepository;

	//모든 주별 TopN 계산 로직 추가
	@Transactional
	public void updateWeeklyRanks(LocalDate weekStart, LocalDate weekEnd) {

		// 모든 회원 조회
		List<Member> allMembers = memberRepository.findAll();
		List<Long> memberIds = allMembers.stream()
			.map(Member::getId)
			.toList();

		Map<Long, BigDecimal> memberPoints = Optional.ofNullable(
				pointTransactionQueryRepository.findEarnedPointByMember(memberIds))
			.orElse(new HashMap<>());

		Map<Long, Integer> certifiedCounts = Optional.ofNullable(
				challengeCertificationRepository.findCertifiedCountByMemberIds(memberIds))
			.orElse(new ArrayList<>())
			.stream()
			.collect(Collectors.toMap(
				MemberCertifiedCountProjection::getMemberId,
				MemberCertifiedCountProjection::getCertifiedCount
			));

		Map<Long, WeeklyRanking> existingMap = weeklyRankingRepository
			.findByWeekStart(weekStart)
			.stream()
			.collect(Collectors.toMap(
				WeeklyRanking::getMemberId,
				w -> w
			));

		List<WeeklyRanking> updatedRanks = new ArrayList<>();

		for (Member member : allMembers) {

			BigDecimal point = memberPoints.getOrDefault(member.getId(), BigDecimal.ZERO);

			int certificationCount = certifiedCounts.getOrDefault(member.getId(), 0);

			String profileImageUrl = (member.getProfile() != null) ?
				member.getProfile().getProfileImageUrl() : null;

			WeeklyRanking existing = existingMap.get(member.getId());

			if (existing != null) {
				existing.updatePointAndCertification(point, certificationCount);
				updatedRanks.add(existing);
			}
		}

		// 정렬 (포인트 내림차순, 동점이면 인증 수 내림차순)
		updatedRanks.sort(
			Comparator.comparing(WeeklyRanking::getTotalPoint, Comparator.reverseOrder())
				.thenComparing(WeeklyRanking::getCertificationCount, Comparator.reverseOrder())
		);

		int ranking = 1;
		for (int i = 0; i < updatedRanks.size(); i++) {

			if (i > 0 &&
				updatedRanks.get(i).getTotalPoint().compareTo(updatedRanks.get(i - 1).getTotalPoint()) == 0 &&
				updatedRanks.get(i).getCertificationCount() ==
					updatedRanks.get(i - 1).getCertificationCount()) {

				updatedRanks.get(i).updateRank(updatedRanks.get(i - 1).getRank());
			} else {
				updatedRanks.get(i).updateRank(ranking);
			}

			ranking++;
		}

		weeklyRankingRepository.saveAll(updatedRanks);
	}

	public List<TopMemberPointResponse> getAllRankData(LocalDate weekStart, int topN) {

		// 상위 N명 랭킹 엔티티 조회
		List<WeeklyRanking> topMembersFromDb = weeklyRankingRepository.findTop8ByWeekStart(weekStart);

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
