package com.example.green.domain.challengecert.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "챌린지 인증 생성 요청")
@Builder
public record ChallengeCertificationCreateRequestDto(
	@Schema(description = "인증 날짜 (미래 날짜 제외)", example = "2024-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "인증 날짜는 필수값입니다.")
	LocalDate certificationDate,

	@Schema(description = "인증 이미지 URL", example = "https://example.com/image.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "인증 이미지 URL은 필수값입니다. 이미지 API에서 purpose를 \"challenge-cert\"로 올린 url을 올려주세요.")
	@Size(max = 500, message = "인증 이미지 URL은 500자 이하여야 합니다.")
	String certificationImageUrl,

	@Schema(description = "인증 후기 (최대 한글 45자)", example = "오늘도 열심히 운동했습니다!")
	@Size(max = 45, message = "인증 후기는 최대 45자까지 입력 가능합니다.")
	String certificationReview
) {
} 