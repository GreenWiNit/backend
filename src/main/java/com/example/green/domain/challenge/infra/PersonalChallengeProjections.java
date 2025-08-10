package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
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
			personalChallenge.challengePoint
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
			personalChallenge.challengePoint,
			exists
		);
	}

	public static ConstructorExpression<AdminPersonalChallengesDto> toChallengesForAdmin() {
		return Projections.constructor(
			AdminPersonalChallengesDto.class,
			personalChallenge.id,
			personalChallenge.challengeCode,
			personalChallenge.challengeName,
			personalChallenge.beginDateTime,
			personalChallenge.endDateTime,
			personalChallenge.challengePoint,
			personalChallenge.displayStatus,
			personalChallenge.createdDate
		);
	}
}
