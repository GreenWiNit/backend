package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 목록 응답")
public record ChallengeDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 이름", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "시작 일시")
	LocalDate beginDate,

	@Schema(description = "종료 일시")
	LocalDate endDate,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/image.jpg")
	String challengeImage,

	@Schema(description = "챌린지 포인트", example = "100")
	BigDecimal challengePoint
) {
}
