package com.example.green.domain.member.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 탈퇴 회원 목록 조회 응답")
public record WithdrawnMemberListResponseDto(
	@Schema(description = "회원키 (고유 식별자)", example = "naver 123456789")
	String memberKey,
	
	@Schema(description = "이메일 (참고용)", example = "user@naver.com")
	String email,
	
	@Schema(description = "닉네임", example = "홍길동")
	String nickname,
	
	@Schema(description = "전화번호", example = "010-1234-5678")
	String phoneNumber,
	
	@Schema(description = "가입일", example = "2025-01-15")
	LocalDateTime joinDate,
	
	@Schema(description = "탈퇴일", example = "2025-01-20")
	LocalDateTime withdrawalDate,
	
	@Schema(description = "등급", example = "일반회원")
	String role,
	
	@Schema(description = "소셜 로그인 제공자", example = "naver")
	String provider
) {
	public static WithdrawnMemberListResponseDto from(Member member) {
		return new WithdrawnMemberListResponseDto(
			member.getMemberKey(),
			member.getEmail(),
			member.getProfile().getNickname(),
			member.getPhoneNumber(),
			member.getCreatedDate(),
			member.getModifiedDate(), // 탈퇴일은 modifiedDate로 사용
			member.getRole() == MemberRole.ADMIN ? "관리자" : "일반회원",
			extractProvider(member.getMemberKey())
		);
	}
	
	private static String extractProvider(String memberKey) {
		if (memberKey == null) return "unknown";
		String[] parts = memberKey.split(" ");
		return parts.length > 0 ? parts[0] : "unknown";
	}
} 