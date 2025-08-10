package com.example.green.domain.challengecert.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.point.entity.vo.PointAmount;

/**
 * PersonalChallengeParticipation 테스트
 */
class PersonalChallengeParticipationTest {

	private PersonalChallengeParticipation participation;
	private PersonalChallenge personalChallenge;
	private Member member;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();

		// 테스트용 Member 객체 생성
		member = Member.create("google 123456789", "테스트유저", "test@example.com");

		// 테스트용 PersonalChallenge 생성
		personalChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, now),
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			PointAmount.of(BigDecimal.valueOf(1000)),
			now.minusDays(1),
			now.plusDays(7),
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// 테스트용 PersonalChallengeParticipation 생성
		participation = PersonalChallengeParticipation.create(
			personalChallenge,
			1L,
			now.minusHours(1)
		);
	}

	@Test
	void create_메서드로_개인_챌린지_참여를_생성할_수_있다() {
		// given
		LocalDateTime participatedAt = now.minusMinutes(30);

		// when
		PersonalChallengeParticipation newParticipation = PersonalChallengeParticipation.create(
			personalChallenge,
			1L,
			participatedAt
		);

		// then
		assertThat(newParticipation.getPersonalChallenge()).isEqualTo(personalChallenge);
		assertThat(newParticipation.getMemberId()).isEqualTo(1L);
		assertThat(newParticipation.getParticipatedAt()).isEqualTo(participatedAt);
	}
}
