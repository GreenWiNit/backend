package com.example.green.domain.challengecert.dto;

import com.example.green.domain.challenge.entity.BaseChallenge;
import com.example.green.domain.challenge.enums.ChallengeType;

/**
 * 관리자 챌린지 제목 응답 DTO
 */
public record AdminChallengeTitleResponseDto(
	Long challengeId,
	String challengeName,
	ChallengeType challengeType
) {
	public static AdminChallengeTitleResponseDto from(BaseChallenge challenge) {
		return new AdminChallengeTitleResponseDto(
			challenge.getId(),
			challenge.getChallengeName(),
			challenge.getChallengeType()
		);
	}
}
