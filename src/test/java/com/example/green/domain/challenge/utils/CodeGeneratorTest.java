package com.example.green.domain.challenge.utils;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.enums.ChallengeType;

/**
 * CodeGenerator 유틸리티 테스트
 */
class CodeGeneratorTest {

	@Test
	void 개인_챌린지_코드를_생성할_수_있다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String challengeCode = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, fixedTime);

		// then
		assertThat(challengeCode).startsWith("CH-P-20250109-143521-");
		assertThat(challengeCode).matches("CH-P-20250109-143521-[A-Z0-9]{4}");
		assertThat(challengeCode).hasSize(25); // CH-P-20250109-143521-XXXX (25자)
	}

	@Test
	void 팀_챌린지_코드를_생성할_수_있다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 22);

		// when
		String challengeCode = CodeGenerator.generateChallengeCode(ChallengeType.TEAM, fixedTime);

		// then
		assertThat(challengeCode).startsWith("CH-T-20250109-143522-");
		assertThat(challengeCode).matches("CH-T-20250109-143522-[A-Z0-9]{4}");
		assertThat(challengeCode).hasSize(25); // CH-T-20250109-143522-XXXX (25자)
	}

	@Test
	void 팀_그룹_코드를_생성할_수_있다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 23);

		// when
		String teamGroupCode = CodeGenerator.generateTeamGroupCode(fixedTime);

		// then
		assertThat(teamGroupCode).startsWith("T-20250109-143523-");
		assertThat(teamGroupCode).matches("T-20250109-143523-[A-Z0-9]{4}");
		assertThat(teamGroupCode).hasSize(22); // T-20250109-143523-XXXX (22자)
	}

	@Test
	void 서로_다른_시간에_생성된_코드는_다르다() {
		// given
		LocalDateTime time1 = LocalDateTime.of(2025, 1, 9, 14, 35, 21);
		LocalDateTime time2 = LocalDateTime.of(2025, 1, 9, 14, 35, 22);

		// when
		String code1 = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, time1);
		String code2 = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, time2);

		// then
		assertThat(code1).isNotEqualTo(code2);
	}

	@Test
	void 같은_시간에_생성된_코드는_ULID로_인해_다르다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String code1 = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, fixedTime);
		String code2 = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, fixedTime);

		// then
		// 시간은 같지만 ULID가 다르므로 코드도 달라야 함
		assertThat(code1).isNotEqualTo(code2);
		assertThat(code1.substring(0, 21)).isEqualTo(code2.substring(0, 21)); // 시간 부분은 동일
		assertThat(code1.substring(22)).isNotEqualTo(code2.substring(22)); // ULID 부분은 다름
	}

	@Test
	void 챌린지_타입별로_다른_코드가_생성된다() {
		// given
		LocalDateTime now = LocalDateTime.now();

		// when
		String personalCode = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, now);
		String teamCode = CodeGenerator.generateChallengeCode(ChallengeType.TEAM, now);
		String teamGroupCode = CodeGenerator.generateTeamGroupCode(now);

		// then
		assertThat(personalCode).startsWith("CH-P-");
		assertThat(teamCode).startsWith("CH-T-");
		assertThat(teamGroupCode).startsWith("T-");
		assertThat(personalCode).isNotEqualTo(teamCode);
		assertThat(teamCode).isNotEqualTo(teamGroupCode);
	}

	@Test
	void 기존_호환성_메서드가_정상_작동한다() {
		// given
		LocalDateTime fixedTime = LocalDateTime.of(2025, 1, 9, 14, 35, 21);

		// when
		String legacyCode = CodeGenerator.generate(ChallengeType.PERSONAL, fixedTime);
		String newCode = CodeGenerator.generateChallengeCode(ChallengeType.PERSONAL, fixedTime);

		// then
		assertThat(legacyCode).startsWith("CH-P-20250109-143521-");
		assertThat(newCode).startsWith("CH-P-20250109-143521-");
		// ULID는 매번 다르므로 완전히 같을 필요는 없지만 형식은 동일해야 함
		assertThat(legacyCode).matches("CH-P-20250109-143521-[A-Z0-9]{4}");
		assertThat(newCode).matches("CH-P-20250109-143521-[A-Z0-9]{4}");
	}
} 