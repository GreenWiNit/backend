package com.example.green.domain.certification.ui.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "챌린지 인증 포인트 지급/미지급 처리")
public record CertificationIdentifierDto(
	@NotNull @NotEmpty
	@Schema(description = "인증 식별자 목록", example = "[1, 2, 3]")
	List<Long> certificationIds
) {
}
