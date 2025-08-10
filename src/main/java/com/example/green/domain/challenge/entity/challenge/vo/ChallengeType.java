package com.example.green.domain.challenge.entity.challenge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 유형 열거형
 */
@RequiredArgsConstructor
@Getter
@Schema(description = "챌린지 유형", allowableValues = {"PERSONAL", "TEAM"})
public enum ChallengeType {
	@Schema(description = "개인 챌린지")
	PERSONAL("P", "개인"),

	@Schema(description = "팀 챌린지")
	TEAM("T", "팀");

	private final String code;
	private final String description;
}
