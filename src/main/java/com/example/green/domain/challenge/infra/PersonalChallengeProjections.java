package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

public class PersonalChallengeProjections {

	public static ConstructorExpression<ChallengeListResponseDto> toChallenges() {
		return Projections.constructor(
			ChallengeListResponseDto.class,
			personalChallenge.id,
			personalChallenge.challengeName,
			personalChallenge.beginDateTime,
			personalChallenge.endDateTime,
			personalChallenge.challengeImage,
			personalChallenge.challengePoint.amount
		);
	}

	public static ConstructorExpression<ChallengeDetailDto> toChallengeByMember(BooleanExpression exists) {
		return Projections.constructor(
			ChallengeDetailDto.class,
			personalChallenge.id,
			personalChallenge.challengeName,
			personalChallenge.beginDateTime,
			personalChallenge.endDateTime,
			personalChallenge.challengeImage,
			personalChallenge.challengePoint.amount,
			exists
		);
	}
}
