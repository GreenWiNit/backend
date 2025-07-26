package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "휴대폰 정보 조회 응답")
public record PhoneInfoResponseDto(
	@Schema(description = "휴대폰 번호", example = "010-1234-5678")
	String phoneNumber,

	@Schema(description = "휴대폰 인증 상태", example = "true")
	boolean isAuthenticated
) {
	public static PhoneInfoResponseDto of(Member member, boolean isAuthenticated) {
		return new PhoneInfoResponseDto(
			member.getPhoneNumber(),
			isAuthenticated
		);
	}
}