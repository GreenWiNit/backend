package com.example.green.domain.challenge.utils;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeType;

/**
 * ChallengeCodeGenerator 유틸리티 테스트
 */
class ChallengeCodeGeneratorTest {

	@Test
	void PERSONAL_타입으로_챌린지_코드를_생성한다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String challengeCode = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, fixedTime);

		// then
		assertThat(challengeCode).startsWith("CH-P-20250109-143521-");
		assertThat(challengeCode).hasSize(25);
		
		// ULID 뒷 4자리 확인 (Base32 문자셋)
		String ulidPart = challengeCode.substring(21);
		assertThat(ulidPart).matches("[0-9A-HJKMNP-TV-Z]{4}");
	}

	@Test
	void TEAM_타입으로_챌린지_코드를_생성한다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String challengeCode = ChallengeCodeGenerator.generate(ChallengeType.TEAM, fixedTime);

		// then
		assertThat(challengeCode).startsWith("CH-T-20250109-143521-");
		assertThat(challengeCode).hasSize(25);
		
		// ULID 뒷 4자리 확인 (Base32 문자셋)
		String ulidPart = challengeCode.substring(21);
		assertThat(ulidPart).matches("[0-9A-HJKMNP-TV-Z]{4}");
	}

	@Test
	void 다른_시간으로_생성하면_다른_코드가_생성된다() {
		// given
		LocalDateTime time1 = LocalDateTime.of(2025, 1, 9, 14, 35, 21);
		LocalDateTime time2 = LocalDateTime.of(2025, 1, 9, 14, 35, 22); // 1초 차이

		// when
		String code1 = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, time1);
		String code2 = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, time2);

		// then
		assertThat(code1).isNotEqualTo(code2);
		assertThat(code1).startsWith("CH-P-20250109-143521-");
		assertThat(code2).startsWith("CH-P-20250109-143522-");
	}

	@Test
	void 동일한_시간이라도_ULID로_인해_고유한_코드가_생성된다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String code1 = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, fixedTime);
		String code2 = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, fixedTime);

		// then
		assertThat(code1).isNotEqualTo(code2);
		
		// 시간 부분은 동일하지만 ULID 부분이 다름
		assertThat(code1.substring(0, 21)).isEqualTo(code2.substring(0, 21)); // CH-P-20250109-143521-
		assertThat(code1.substring(21)).isNotEqualTo(code2.substring(21)); // ULID 뒷 4자리
	}

	@Test
	void 생성된_코드는_올바른_형식을_따른다() {
		// given
		LocalDateTime now = LocalDateTime.now();

		// when
		String personalCode = ChallengeCodeGenerator.generate(ChallengeType.PERSONAL, now);
		String teamCode = ChallengeCodeGenerator.generate(ChallengeType.TEAM, now);

		// then
		// 전체 형식 확인: CH-{타입}-{yyyyMMdd}-{HHmmss}-{ULID 뒷 4자리}
		assertThat(personalCode).matches("CH-P-\\d{8}-\\d{6}-[0-9A-HJKMNP-TV-Z]{4}");
		assertThat(teamCode).matches("CH-T-\\d{8}-\\d{6}-[0-9A-HJKMNP-TV-Z]{4}");
		
		// 길이 확인
		assertThat(personalCode).hasSize(25);
		assertThat(teamCode).hasSize(25);
	}
} 
