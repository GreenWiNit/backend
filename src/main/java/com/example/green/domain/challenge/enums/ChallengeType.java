package com.example.green.domain.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 유형 열거형
 */
@RequiredArgsConstructor
@Getter
public enum ChallengeType {
	PERSONAL("P", "개인"),
	TEAM("T", "팀");

	private final String code;
	private final String description;
}
