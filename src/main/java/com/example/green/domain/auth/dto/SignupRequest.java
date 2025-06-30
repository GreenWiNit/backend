package com.example.green.domain.auth.dto;

public record SignupRequest(
	String tempToken,
	String nickname,
	String profileImageUrl
) {
} 