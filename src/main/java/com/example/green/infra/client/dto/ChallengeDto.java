package com.example.green.infra.client.dto;

import com.example.green.domain.challenge.entity.challenge.BaseChallenge;
import com.example.green.domain.challenge.entity.challenge.Challenge;

public record ChallengeDto(
	Long id,
	String name,
	String code,
	String imageUrl,
	Integer point,
	String challengeType
) {

	public static ChallengeDto from(BaseChallenge challenge) {
		return new ChallengeDto(
			challenge.getId(), challenge.getChallengeName(), challenge.getChallengeCode(),
			challenge.getChallengeImage(),
			challenge.getChallengePoint().intValue(),
			challenge.getChallengeType().getCode()
		);
	}

	public static ChallengeDto from(Challenge challenge) {
		return new ChallengeDto(
			challenge.getId(),
			challenge.getInfo().getName(),
			challenge.getCode(),
			challenge.getImageUrl(),
			challenge.getInfo().getPoint(),
			challenge.getType().getCode()
		);
	}
}
