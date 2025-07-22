package com.example.green.domain.challenge.repository;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.ChallengeCodeGenerator;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.global.api.page.CursorTemplate;
import com.example.integration.common.ServiceIntegrationTest;

/**
 * TeamChallengeRepository 통합 테스트
 */
class TeamChallengeRepositoryTest extends ServiceIntegrationTest {

	@Autowired
	private TeamChallengeRepository repository;

	private TeamChallenge teamChallenge1;
	private TeamChallenge teamChallenge2;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 진행 중인 팀 챌린지
		teamChallenge1 = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now),
			"진행 중인 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge1-image.jpg",
			"진행 중인 팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 완료된 팀 챌린지
		teamChallenge2 = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now.plusMinutes(1)),
			"완료된 팀 챌린지",
			ChallengeStatus.COMPLETED,
			PointAmount.of(BigDecimal.valueOf(3000)),
			now.minusDays(14),
			now.minusDays(7),
			10,
			"challenge2-image.jpg",
			"완료된 팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		repository.save(teamChallenge1);
		repository.save(teamChallenge2);
	}

	@Test
	void 저장된_데이터_확인() {
		// when
		List<TeamChallenge> allChallenges = repository.findAll();

		// then
		System.out.println("Total challenges: " + allChallenges.size());
		for (TeamChallenge challenge : allChallenges) {
			System.out.println("Challenge: " + challenge.getChallengeName()
				+ ", Status: " + challenge.getChallengeStatus()
				+ ", Begin: " + challenge.getBeginDateTime()
				+ ", End: " + challenge.getEndDateTime()
				+ ", Now: " + now);
		}
	}

	@Test
	void 커서_기반으로_팀_챌린지를_조회할_수_있다() {
		// when
		List<TeamChallenge> allChallenges = repository.findAll();
		CursorTemplate<Long, ChallengeListResponseDto> result = repository.findTeamChallengesByCursor(
			null, 10, ChallengeStatus.PROCEEDING, teamChallenge1.getBeginDateTime().plusHours(1)
		);

		// then
		System.out.println("All challenges: " + allChallenges.size());
		System.out.println("Found challenges: " + result.content().size());
		assertThat(result.content()).isNotEmpty();
		assertThat(result.content().get(0).challengeName()).isEqualTo("진행 중인 팀 챌린지");
	}

	@Test
	void 완료된_상태의_팀_챌린지는_현재_진행_중_조회에서_제외된다() {
		// when
		CursorTemplate<Long, ChallengeListResponseDto> result = repository.findTeamChallengesByCursor(
			null, 10, ChallengeStatus.PROCEEDING, now
		);

		// then
		assertThat(result.content()).hasSize(1);
		assertThat(result.content())
			.extracting(ChallengeListResponseDto::challengeName)
			.containsOnly("진행 중인 팀 챌린지");
	}

	@Test
	void 커서_제한으로_특정_개수만큼_조회할_수_있다() {
		// given
		// 추가 팀 챌린지 생성
		TeamChallenge additionalChallenge = TeamChallenge.create(
			ChallengeCodeGenerator.generate(ChallengeType.TEAM, now.plusMinutes(2)),
			"추가 팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1500)),
			now.minusDays(1),
			now.plusDays(7),
			3,
			"additional-image.jpg",
			"추가 팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);
		repository.save(additionalChallenge);

		// when
		CursorTemplate<Long, ChallengeListResponseDto> result = repository.findTeamChallengesByCursor(
			null, 1, ChallengeStatus.PROCEEDING, now
		);

		// then
		assertThat(result.content()).hasSize(1);
	}

	@Test
	void 현재_시간이_챌린지_기간에_포함되지_않으면_조회되지_않는다() {
		// given
		LocalDateTime futureTime = now.plusDays(10);

		// when
		CursorTemplate<Long, ChallengeListResponseDto> result = repository.findTeamChallengesByCursor(
			null, 10, ChallengeStatus.PROCEEDING, futureTime
		);

		// then
		assertThat(result.content()).isEmpty();
	}
} 