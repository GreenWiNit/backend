package com.example.green.domain.challenge.entity;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.domain.point.entity.vo.PointAmount;

/**
 * PersonalChallenge 엔티티 테스트
 */
class PersonalChallengeTest {

	private PersonalChallenge personalChallenge;
	private PointAmount challengePoint;
	private LocalDateTime now;

	@BeforeEach
	void setUp() {
		now = LocalDateTime.now();
		challengePoint = PointAmount.of(BigDecimal.valueOf(1000));

		personalChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, now),
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			now.minusDays(1), // 어제 시작
			now.plusDays(1),   // 내일 종료
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);
	}

	@Test
	void 진행중이고_종료_시간_이전이면_참여_가능하다() {
		// given
		LocalDateTime testNow = now;
		PersonalChallenge participableChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, testNow),
			"참여 가능한 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			testNow.minusDays(1),
			testNow.plusHours(1), // 1시간 후 종료
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// when
		boolean canParticipate = participableChallenge.isActive(testNow);

		// then
		assertTrue(canParticipate);
	}

	@Test
	void 진행중이_아니면_참여할_수_없다() {
		// given
		LocalDateTime testNow = now;
		PersonalChallenge deadlineChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, testNow),
			"마감된 챌린지",
			ChallengeStatus.DEADLINE,
			challengePoint,
			testNow.minusDays(1),
			testNow.plusHours(1),
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// when
		boolean canParticipate = deadlineChallenge.isActive(testNow);

		// then
		assertFalse(canParticipate);
	}

	@Test
	void 종료_시간이_지나면_참여할_수_없다() {
		// given
		LocalDateTime testNow = now;
		PersonalChallenge expiredChallenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, testNow),
			"만료된 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			testNow.minusDays(2),
			testNow.minusHours(1), // 1시간 전 종료
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// when
		boolean canParticipate = expiredChallenge.isActive(testNow);

		// then
		assertFalse(canParticipate);
	}

	@Test
	void PersonalChallenge는_PERSONAL_타입을_가진다() {
		// given
		PersonalChallenge challenge = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, LocalDateTime.now()),
			"개인 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
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
		assertThat(challengeCode).hasSize(25); // CH-P-20250109-143521-A3FV 형태 (25자)
		// 날짜, 시간, ULID 뒷 4자리 형식 확인
		assertThat(challengeCode).matches("CH-P-\\d{8}-\\d{6}-[0-9A-HJKMNP-TV-Z]{4}");
	}

	@Test
	void 각_PersonalChallenge마다_고유한_challengeCode를_가진다() throws InterruptedException {
		// given
		LocalDateTime now1 = LocalDateTime.now();
		Thread.sleep(1); // 시간 차이를 만들기 위해
		LocalDateTime now2 = LocalDateTime.now();

		PersonalChallenge challenge1 = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, now1),
			"첫 번째 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"첫 번째 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		PersonalChallenge challenge2 = PersonalChallenge.create(
			CodeGenerator.generate(ChallengeType.PERSONAL, now2),
			"두 번째 챌린지",
			ChallengeStatus.PROCEEDING,
			challengePoint,
			LocalDateTime.now(),
			LocalDateTime.now().plusDays(1),
			"challenge-image.jpg",
			"두 번째 챌린지 설명",
			ChallengeDisplayStatus.VISIBLE
		);

		// when
		String code1 = challenge1.getChallengeCode();
		String code2 = challenge2.getChallengeCode();

		// then
		assertThat(code1).isNotEqualTo(code2);
		assertThat(code1).startsWith("CH-P-");
		assertThat(code2).startsWith("CH-P-");
	}

	@Test
	void ULID_기반_challengeCode는_Base32_문자셋을_사용한다() {
		// when
		String challengeCode = personalChallenge.getChallengeCode();

		// then
		// ULID는 Crockford Base32를 사용 (0123456789ABCDEFGHJKMNPQRSTVWXYZ, I,L,O,U 제외)
		String ulidPart = challengeCode.substring(21); // 뒷 4자리
		assertThat(ulidPart).matches("[0-9A-HJKMNP-TV-Z]{4}");

		// I, L, O, U가 포함되지 않는지 확인
		assertThat(ulidPart).doesNotContain("I", "L", "O", "U");
	}
}
