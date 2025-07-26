package com.example.green.domain.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 전시 상태 열거형
 */
@RequiredArgsConstructor
@Getter
public enum ChallengeDisplayStatus {
	HIDDEN("숨김"),
	VISIBLE("전시");
	// PINNED("고정");

	private final String description;
} 