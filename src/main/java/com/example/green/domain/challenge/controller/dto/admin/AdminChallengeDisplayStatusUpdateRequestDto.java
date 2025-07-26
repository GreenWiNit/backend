package com.example.green.domain.challenge.controller.dto.admin;

import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "어드민 챌린지 전시 상태 변경 요청")
public record AdminChallengeDisplayStatusUpdateRequestDto(
	@Schema(description = "전시 상태 (VISIBLE: 사용자에게 보임, HIDDEN: 사용자에게 보이지 않음)",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "VISIBLE")
	@NotNull(message = "전시 상태는 필수값입니다.")
	ChallengeDisplayStatus displayStatus
) {
}
