package com.example.green.global.client.dto;

import java.time.LocalDate;

import com.example.green.domain.challenge.entity.challenge.BaseChallenge;

public record ChallengeDto(
	Long id,
	String name,
	String code,
	String imageUrl,
	Integer point,
	LocalDate beginDate,
	LocalDate endDate,
	String challengeType
) {

	public static ChallengeDto from(BaseChallenge challenge) {
		return new ChallengeDto(
			challenge.getId(), challenge.getChallengeName(), challenge.getChallengeCode(),
			challenge.getChallengeImage(),
			challenge.getChallengePoint().intValue(),
			challenge.getBeginDate(),
			challenge.getEndDate(),
			challenge.getChallengeType().getCode()
		);
	}
}
