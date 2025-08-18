package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 상세 응답")
public record ChallengeDetailDtoV2(
	@Schema(description = "팀 챌린지 ID", example = "1")
	Long id,
	@Schema(description = "챌린지명", example = "플러깅")
	String challengeName,
	@Schema(description = "챌린지 시작일")
	LocalDate beginDate,
	@Schema(description = "챌린지 종료일")
	LocalDate endDate,
	@Schema(description = "챌린지 이미지")
	String challengeImage,
	@Schema(description = "챌린지 정보")
	String challengeContent,
	@Schema(description = "챌린지 포인트", example = "100")
	BigDecimal challengePoint,
	@Schema(description = "참여 여부")
	boolean participating
) {
}
