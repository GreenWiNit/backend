package com.example.green.domain.challengecert.dto;

/**
 * 관리자 팀 챌린지 그룹 코드 응답 DTO  
 */
public record AdminGroupCodeResponseDto(
	String groupCode,
	String groupName,
	Integer participantCount
) {
	public static AdminGroupCodeResponseDto of(String groupCode, String groupName, Integer participantCount) {
		return new AdminGroupCodeResponseDto(groupCode, groupName, participantCount);
	}
}
