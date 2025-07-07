package com.example.green.domain.auth.service.result;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "휴대전화 인증 요청 결과")
public record PhoneVerificationResult(
	@Schema(description = "인증 토큰", example = "1aB2cD3eF4gH5iJ6")
	String token,
	@Schema(description = "서버 이메일 주소", example = "greenwinit.verify@gmail.com")
	String serverEmailAddress
) {
}
