package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 기본 정보 응답")
public record MemberInfoResponseDto(
    
    @Schema(description = "닉네임", example = "환경지킴이")
    String nickname,
    
    @Schema(description = "이메일", example = "user@example.com")
    String email,
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg", nullable = true)
    String profileImageUrl
    
) {
    
    public static MemberInfoResponseDto from(Member member) {
        return new MemberInfoResponseDto(
            member.getProfile().getNickname(),
            member.getEmail(),
            member.getProfile().getProfileImageUrl()
        );
    }
} 