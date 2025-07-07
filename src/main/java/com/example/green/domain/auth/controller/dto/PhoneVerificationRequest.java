package com.example.green.domain.auth.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "휴대전화 인증 요청")
public record PhoneVerificationRequest(
	@Schema(description = "휴대전화번호", example = "010-1234-5678")
	@NotBlank(message = "전화번호는 필수 입니다.")
	String phoneNumber
) {
}
