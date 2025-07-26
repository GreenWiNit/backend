package com.example.green.domain.challenge.controller.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "어드민 챌린지 이미지 업데이트 요청")
public record AdminChallengeImageUpdateRequestDto(
	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/challenge-image.jpg")
	@NotBlank(message = "챌린지 이미지 URL은 필수값입니다.")
	String challengeImageUrl
) {
}
