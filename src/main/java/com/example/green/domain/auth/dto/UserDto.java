package com.example.green.domain.auth.dto;

public record UserDto(
	String role,
	String name,
	String memberKey,
	boolean isNewUser,
	OAuth2UserInfoDto oauth2UserInfoDto
) {

	// 기존 사용자용 생성자
	public static UserDto forExistingUser(String role, String name, String memberKey) {
		return new UserDto(role, name, memberKey, false, null);
	}

	// 신규 사용자용 생성자
	public static UserDto forNewUser(OAuth2UserInfoDto oauth2UserInfoDto) {
		return new UserDto(
			"ROLE_USER",
			oauth2UserInfoDto.name(),
			oauth2UserInfoDto.getMemberKey(),
			true,
			oauth2UserInfoDto
		);
	}
}
