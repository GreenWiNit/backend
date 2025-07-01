package com.example.green.domain.challenge.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 상태 열거형
 */
@RequiredArgsConstructor
@Getter
public enum ChallengeStatus {
	PROCEEDING("진행중"),
	DEADLINE("마감"),
	COMPLETED("종료");

	private final String description;
}
