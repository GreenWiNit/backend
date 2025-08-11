package com.example.green.domain.challenge.infra.querydsl.projections;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.PersonalParticipationDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChallengeProjections {

	public static ConstructorExpression<ChallengeDto> toChallenges() {
		return Projections.constructor(
			ChallengeDto.class,
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

	public static ConstructorExpression<PersonalParticipationDto> toParticipationForAdmin() {
		return Projections.constructor(
			PersonalParticipationDto.class,
			personalChallengeParticipation.memberId,
			personalChallengeParticipation.participatedAt,
			personalChallengeParticipation.certCount
		);
	}
}
