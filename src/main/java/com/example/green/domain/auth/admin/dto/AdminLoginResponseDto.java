package com.example.green.domain.auth.admin.dto;

import com.example.green.domain.auth.admin.entity.Admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminLoginResponseDto {

	private final String accessToken;
	private final String loginId;
	private final String name;
	private final String role;

	public static AdminLoginResponseDto of(String accessToken, Admin admin) {
		return new AdminLoginResponseDto(
			accessToken,
			admin.getLoginId(),
			admin.getName(),
			Admin.ROLE_ADMIN
		);
	}
} 