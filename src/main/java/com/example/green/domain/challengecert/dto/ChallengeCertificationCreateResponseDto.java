package com.example.green.domain.challengecert.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "챌린지 인증 생성 응답")
@Builder
public record ChallengeCertificationCreateResponseDto(
	@Schema(description = "생성된 인증 ID", example = "1")
	Long certificationId
) {
} 