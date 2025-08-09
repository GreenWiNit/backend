package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;

public class TeamChallengeProjections {

	public static ConstructorExpression<ChallengeListResponseDto> toChallenges() {
		return Projections.constructor(
			ChallengeListResponseDto.class,
			teamChallenge.id,
			teamChallenge.challengeName,
			teamChallenge.beginDateTime,
			teamChallenge.endDateTime,
			teamChallenge.challengeImage,
			teamChallenge.challengePoint
		);
	}
}
