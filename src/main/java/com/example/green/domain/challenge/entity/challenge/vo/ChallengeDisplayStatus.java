package com.example.green.domain.challenge.entity.challenge.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 챌린지 전시 상태 열거형
 */
@RequiredArgsConstructor
@Getter
@Schema(description = "챌린지 전시 상태", allowableValues = {"HIDDEN", "VISIBLE"})
public enum ChallengeDisplayStatus {
	@Schema(description = "숨김 - 사용자에게 보이지 않음")
	HIDDEN("숨김"),

	@Schema(description = "전시 - 사용자에게 보임")
	VISIBLE("전시");
	// PINNED("고정");

	private final String description;
}
