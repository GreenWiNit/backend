package com.example.green.domain.member.repository.dto;

public record UserBasicInfo(
	Long memberId,
	String nickname,
	String profileImageUrl
) {
}
