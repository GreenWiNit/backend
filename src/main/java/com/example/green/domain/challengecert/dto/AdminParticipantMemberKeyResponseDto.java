package com.example.green.domain.challengecert.dto;

/**
 * 관리자 챌린지 참여자 memberKey 응답 DTO
 */
public record AdminParticipantMemberKeyResponseDto(
	String memberKey,
	String nickname
) {
	public static AdminParticipantMemberKeyResponseDto of(String memberKey, String nickname) {
		return new AdminParticipantMemberKeyResponseDto(memberKey, nickname);
	}
}
