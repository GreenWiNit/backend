package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileUpdateResponseDto {

	private final String nickname;
	private final String profileImageUrl;

	public static ProfileUpdateResponseDto from(Member member) {
		return new ProfileUpdateResponseDto(
			member.getProfile().getNickname(),
			member.getProfile().getProfileImageUrl()
		);
	}
} 