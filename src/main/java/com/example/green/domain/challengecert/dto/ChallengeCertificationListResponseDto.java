package com.example.green.domain.challengecert.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "챌린지 인증 목록 조회 응답")
@Builder
public record ChallengeCertificationListResponseDto(
	@Schema(description = "인증 ID", example = "123")
	Long certificationId,

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "30일 런닝 챌린지")
	String challengeTitle,

	@Schema(description = "인증 날짜", example = "2024-01-15")
	LocalDate certifiedDate,

	@Schema(description = "승인 여부", example = "true")
	Boolean approved
) {
}
