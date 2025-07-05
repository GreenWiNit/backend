package com.example.green.domain.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record PhoneVerificationRequest(
	@NotBlank(message = "전화번호는 필수 입니다.")
	String phoneNumber
) {
}
