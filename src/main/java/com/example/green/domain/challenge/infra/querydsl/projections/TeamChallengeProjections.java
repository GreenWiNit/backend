package com.example.green.domain.challenge.infra.querydsl.projections;

import static com.example.green.domain.challenge.entity.challenge.QTeamChallenge.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamChallengeProjections {

	public static ConstructorExpression<ChallengeDto> toChallenges() {
		return Projections.constructor(
			ChallengeDto.class,
			teamChallenge.id,
			teamChallenge.challengeName,
			teamChallenge.beginDateTime,
			teamChallenge.endDateTime,
			teamChallenge.challengeImage,
			teamChallenge.challengePoint
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
			teamChallenge.challengePoint,
			exists
		);
	}

	public static ConstructorExpression<AdminTeamChallengesDto> toChallengesForAdmin() {
		return Projections.constructor(
			AdminTeamChallengesDto.class,
			teamChallenge.id,
			teamChallenge.challengeCode,
			teamChallenge.challengeName,
			teamChallenge.beginDateTime,
			teamChallenge.endDateTime,
			teamChallenge.challengePoint,
			teamChallenge.teamCount,
			teamChallenge.displayStatus,
			teamChallenge.createdDate
		);
	}
}
