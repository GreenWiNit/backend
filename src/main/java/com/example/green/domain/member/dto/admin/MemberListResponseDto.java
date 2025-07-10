package com.example.green.domain.member.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 회원 목록 조회 응답")
public record MemberListResponseDto(
	@Schema(description = "사용자명 (고유 식별자)", example = "naver 123456789")
	String username,
	
	@Schema(description = "이메일 (참고용)", example = "user@naver.com")
	String email,
	
	@Schema(description = "닉네임", example = "홍길동")
	String nickname,
	
	@Schema(description = "전화번호", example = "010-1234-5678")
	String phoneNumber,
	
	@Schema(description = "가입일", example = "2025-01-15")
	LocalDateTime joinDate,
	
	@Schema(description = "등급", example = "일반회원")
	String role,
	
	@Schema(description = "소셜 로그인 제공자", example = "naver")
	String provider
) {
	public static MemberListResponseDto from(Member member) {
		return new MemberListResponseDto(
			member.getUsername(),
			member.getEmail(),
			member.getProfile().getNickname(),
			member.getPhoneNumber(),
			member.getCreatedDate(),
			member.getRole() == MemberRole.ADMIN ? "관리자" : "일반회원",
			extractProvider(member.getUsername())
		);
	}
	
	private static String extractProvider(String username) {
		if (username == null) return "unknown";
		String[] parts = username.split(" ");
		return parts.length > 0 ? parts[0] : "unknown";
	}
} 