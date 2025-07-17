package com.example.green.domain.challenge.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.global.utils.UlidUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 챌린지 코드 생성 유틸리티
 * 형식: CH-{타입}-{생성일자}-{시간}-{ULID 뒷 4자리}
 * 예: CH-P-20250109-143521-A3FV, CH-T-20250109-143522-B7MX
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeCodeGenerator {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");

	/**
	 * 챌린지 코드를 생성합니다.
	 *
	 * @param challengeType 챌린지 타입 (PERSONAL 또는 TEAM)
	 * @param now 현재 시간
	 * @return 생성된 챌린지 코드
	 */
	public static String generate(ChallengeType challengeType, LocalDateTime now) {
		String dateCode = now.format(DATE_FORMATTER);
		String timeCode = now.format(TIME_FORMATTER);
		String ulidSuffix = UlidUtils.generate().substring(22);  // 뒷 4자리

		return String.format("CH-%s-%s-%s-%s",
			challengeType.getCode(),
			dateCode,
			timeCode,
			ulidSuffix);
	}
}
