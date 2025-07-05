package com.example.green.domain.auth.service.result;

public record PhoneVerificationResult(
	String token,
	String serverEmailAddress
) {
}
