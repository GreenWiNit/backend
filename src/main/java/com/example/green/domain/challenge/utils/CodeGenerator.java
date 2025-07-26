package com.example.green.domain.challenge.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.global.utils.UlidUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 코드 생성 유틸리티
 * 챌린지 코드 형식: CH-{타입}-{생성일자}-{시간}-{ULID 뒷 4자리}
 * 팀 그룹 코드 형식: T-{생성일자}-{시간}-{ULID 뒷 4자리}
 * 예: CH-P-20250109-143521-A3FV, CH-T-20250109-143522-B7MX, T-20250109-143523-C8NQ
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CodeGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * 챌린지 코드를 생성합니다.
	 *
	 * @param challengeType 챌린지 타입 (PERSONAL 또는 TEAM)
	 * @param now 현재 시간
	 * @return 생성된 챌린지 코드
	 */
	public static String generateChallengeCode(ChallengeType challengeType, LocalDateTime now) {
		String dateCode = now.format(DATE_FORMATTER);
		String timeCode = now.format(TIME_FORMATTER);
		String ulidSuffix = UlidUtils.generate().substring(22);  // 뒷 4자리

		return String.format("CH-%s-%s-%s-%s",
			challengeType.getCode(),
			dateCode,
			timeCode,
			ulidSuffix);
	}

	/**
	 * 팀 그룹 코드를 생성합니다.
	 *
	 * @param now 현재 시간
	 * @return 생성된 팀 그룹 코드
	 */
	public static String generateTeamGroupCode(LocalDateTime now) {
		String dateCode = now.format(DATE_FORMATTER);
		String timeCode = now.format(TIME_FORMATTER);
		String ulidSuffix = UlidUtils.generate().substring(22);  // 뒷 4자리

		return String.format("T-%s-%s-%s",
			dateCode,
			timeCode,
			ulidSuffix);
	}

	/**
	 * 기존 호환성을 위한 메서드
	 */
	@Deprecated
	public static String generate(ChallengeType challengeType, LocalDateTime now) {
		return generateChallengeCode(challengeType, now);
	}
}
