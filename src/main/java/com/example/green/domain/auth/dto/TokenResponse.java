package com.example.green.domain.auth.dto;

public record TokenResponse(
	String accessToken,
	String username,
	String userName
) {
}
