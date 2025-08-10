package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

public class TeamChallengeProjections {

	public static ConstructorExpression<ChallengeListResponseDto> toChallenges() {
		return Projections.constructor(
			ChallengeListResponseDto.class,
			teamChallenge.id,
			teamChallenge.challengeName,
			teamChallenge.beginDateTime,
			teamChallenge.endDateTime,
			teamChallenge.challengeImage,
			teamChallenge.challengePoint.amount
		);
	}

	public static ConstructorExpression<ChallengeDetailDto> toChallengeByMember(BooleanExpression exists) {
		return Projections.constructor(
			ChallengeDetailDto.class,
			teamChallenge.id,
			teamChallenge.challengeName,
			teamChallenge.beginDateTime,
			teamChallenge.endDateTime,
			teamChallenge.challengeImage,
			teamChallenge.challengePoint.amount,
			exists
		);
	}
}
