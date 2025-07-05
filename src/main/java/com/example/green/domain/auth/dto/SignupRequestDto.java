package com.example.green.domain.auth.dto;

public record SignupRequestDto(
	String tempToken,
	String nickname,
	String profileImageUrl
) {
}
