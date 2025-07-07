package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.point.entity.vo.PointAmount;

class PersonalChallengeTest {

	private PersonalChallenge personalChallenge;
	private PointAmount challengePoint;

	@BeforeEach
	void setUp() {
		challengePoint = PointAmount.of(BigDecimal.valueOf(1000));
		personalChallenge = PersonalChallenge.create(
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1), // 어제 시작
			LocalDateTime.now().plusDays(1),   // 내일 종료
			"challenge-image.jpg",
			"챌린지 설명"
		);
	}

	@Test
	void 진행중이고_현재_시간이_챌린지_기간_내에_있으면_활성_상태이다() {
		// given
		PersonalChallenge activeChallenge = PersonalChallenge.create(
			"활성 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusHours(1), // 1시간 전 시작
			LocalDateTime.now().plusHours(1),   // 1시간 후 종료
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean isActive = activeChallenge.isActive();

		// then
		assertTrue(isActive);
	}

	@Test
	void 진행중이_아닌_상태면_활성_상태가_아니다() {
		// given
		PersonalChallenge completedChallenge = PersonalChallenge.create(
			"완료된 챌린지",
			ChallengeStatus.COMPLETED,
			challengePoint,
			LocalDateTime.now().minusDays(2),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean isActive = completedChallenge.isActive();

		// then
		assertFalse(isActive);
	}

	@Test
	void 시작_시간_이전이면_활성_상태가_아니다() {
		// given
		PersonalChallenge futureChallenge = PersonalChallenge.create(
			"미래 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().plusHours(1), // 1시간 후 시작
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean isActive = futureChallenge.isActive();

		// then
		assertFalse(isActive);
	}

	@Test
	void 종료_시간_이후면_활성_상태가_아니다() {
		// given
		PersonalChallenge expiredChallenge = PersonalChallenge.create(
			"만료된 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(2),
			LocalDateTime.now().minusHours(1), // 1시간 전 종료
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean isActive = expiredChallenge.isActive();

		// then
		assertFalse(isActive);
	}

	@Test
	void 진행중이고_종료_시간_이전이면_참여_가능하다() {
		// given
		PersonalChallenge participableChallenge = PersonalChallenge.create(
			"참여 가능한 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusHours(1), // 1시간 후 종료
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean canParticipate = participableChallenge.canParticipate();

		// then
		assertTrue(canParticipate);
	}

	@Test
	void 진행중이_아니면_참여할_수_없다() {
		// given
		PersonalChallenge deadlineChallenge = PersonalChallenge.create(
			"마감된 챌린지",
			ChallengeStatus.DEADLINE,
			challengePoint,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusHours(1),
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean canParticipate = deadlineChallenge.canParticipate();

		// then
		assertFalse(canParticipate);
	}

	@Test
	void 종료_시간이_지나면_참여할_수_없다() {
		// given
		PersonalChallenge expiredChallenge = PersonalChallenge.create(
			"만료된 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now().minusDays(2),
			LocalDateTime.now().minusHours(1), // 1시간 전 종료
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		boolean canParticipate = expiredChallenge.canParticipate();

		// then
		assertFalse(canParticipate);
	}

	@Test
	void PersonalChallenge는_PERSONAL_타입을_가진다() {
		// given
		PersonalChallenge challenge = PersonalChallenge.create(
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"챌린지 설명"
		);

		// when
		ChallengeType type = challenge.getChallengeType();

		// then
		assertThat(type).isEqualTo(ChallengeType.PERSONAL);
	}

	@Test
	void PersonalChallenge_생성_시_challengeCode가_자동으로_생성된다() {
		// when
		String challengeCode = personalChallenge.getChallengeCode();

		// then
		assertThat(challengeCode).isNotNull();
		assertThat(challengeCode).startsWith("CH-P-");
		assertThat(challengeCode).hasSize(21); // CH-P-20250109-1435217 형태 (21자)
		// 날짜 부분 확인
		assertThat(challengeCode).matches("CH-P-\\d{8}-\\d{7}");
	}

	@Test
	void 각_PersonalChallenge마다_고유한_challengeCode를_가진다() throws InterruptedException {
		// given
		PersonalChallenge challenge1 = PersonalChallenge.create(
			"첫 번째 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"첫 번째 챌린지 설명"
		);

		// 동일한 밀리초에 생성되는 것을 방지하기 위해 약간의 지연
		Thread.sleep(1);

		PersonalChallenge challenge2 = PersonalChallenge.create(
			"두 번째 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"두 번째 챌린지 설명"
		);

		// when
		String code1 = challenge1.getChallengeCode();
		String code2 = challenge2.getChallengeCode();

		// then
		// 현재는 시간 기반으로 생성되어 거의 항상 고유하지만,
		// 향후 시퀀스 테이블이나 Redis로 변경 시 완전한 고유성 보장
		assertThat(code1).isNotEqualTo(code2);
		assertThat(code1).startsWith("CH-P-");
		assertThat(code2).startsWith("CH-P-");
	}

	@Test
	void 챌린지_코드가_시간_기반으로_올바르게_생성된다() {
		// when
		PersonalChallenge challenge = PersonalChallenge.create(
			"시간 기반 테스트 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"시간 기반 테스트 설명"
		);

		// then
		String challengeCode = challenge.getChallengeCode();
		String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		// 형식 검증: CH-P-yyyyMMdd-HHmmsss (시분초 + 밀리초 마지막 자리)
		assertThat(challengeCode).matches("CH-P-\\d{8}-\\d{7}");
		assertThat(challengeCode).contains(today); // 오늘 날짜 포함
		assertThat(challengeCode).hasSize(21);

		// 타입 확인
		assertThat(challengeCode).startsWith("CH-P-");

		// 시간 부분이 숫자인지 확인 (마지막 7자리)
		String timePart = challengeCode.substring(challengeCode.length() - 7);
		assertThat(timePart).matches("\\d{7}");
	}
}
