package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;

/**
 * TeamChallengeParticipation 테스트
 */
class TeamChallengeParticipationTest {

	private TeamChallengeParticipation participation;
	private TeamChallenge teamChallenge;
	private Member member;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("google 123456789", "테스트유저", "test@example.com");

		// 테스트용 TeamChallenge 생성
		teamChallenge = TeamChallenge.create(
			CodeGenerator.generate(ChallengeType.TEAM, now),
			"팀 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(2000)),
			now.minusDays(1),
			now.plusDays(7),
			5,
			"challenge-image.jpg",
			"팀 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 테스트용 TeamChallengeParticipation 생성
		participation = TeamChallengeParticipation.create(
			teamChallenge,
			1L,
			now.minusHours(1)
		);
	}

	@Test
	void create_메서드로_팀_챌린지_참여를_생성할_수_있다() {
		// given
		LocalDateTime participatedAt = now.minusMinutes(30);

		// when
		TeamChallengeParticipation newParticipation = TeamChallengeParticipation.create(
			teamChallenge,
			1L,
			participatedAt
		);

		// then
		assertThat(newParticipation.getTeamChallenge()).isEqualTo(teamChallenge);
		assertThat(newParticipation.getMemberId()).isEqualTo(1L);
		assertThat(newParticipation.getParticipatedAt()).isEqualTo(participatedAt);
	}
}
