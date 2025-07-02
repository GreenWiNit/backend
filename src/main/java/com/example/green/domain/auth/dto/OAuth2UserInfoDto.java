package com.example.green.domain.auth.dto;

public record OAuth2UserInfoDto(
	String email,
	String name,
	String profileImageUrl,
	String provider,
	String providerId
) {

	public String getUsername() {
		return provider + " " + providerId;
	}
}
