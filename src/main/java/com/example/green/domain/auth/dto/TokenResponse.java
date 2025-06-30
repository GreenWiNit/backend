package com.example.green.domain.auth.dto;

public record TokenResponse(
	String accessToken,
	String refreshToken,
	String username,
	String userName
) {
	// AccessToken만 있는 경우 (토큰 갱신 시)
	public TokenResponse(String accessToken, String username, String userName) {
		this(accessToken, null, username, userName);
	}
} 