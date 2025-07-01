package com.example.green.domain.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 유형 열거형
 */
@RequiredArgsConstructor
@Getter
public enum ChallengeType {
	PERSONAL("개인"),
	TEAM("팀");

	private final String description;
}
