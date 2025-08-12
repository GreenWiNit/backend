package com.example.green.global.client.dto;

import com.example.green.domain.challenge.entity.challenge.BaseChallenge;

public record ChallengeDto(
	Long id,
	String name,
	String code
) {

	public static ChallengeDto from(BaseChallenge challenge) {
		return new ChallengeDto(challenge.getId(), challenge.getChallengeName(), challenge.getChallengeCode());
	}
}
