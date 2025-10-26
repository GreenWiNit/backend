package com.example.green.domain.challenge.infra.querydsl.projections;

import static com.example.green.domain.challenge.entity.challenge.QChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeClientProjections {

	public static ConstructorExpression<ChallengeDto> toMyChallenges() {
		return Projections.constructor(
			ChallengeDto.class,
			challenge.id,
			challenge.info,
			challenge.content.imageUrl,
			challenge.participantCount,
			participation.id
		);
	}

	public static ConstructorExpression<ChallengeDto> toChallenges() {
		return Projections.constructor(
			ChallengeDto.class,
			challenge.id,
			challenge.info,
			challenge.content.imageUrl,
			challenge.participantCount
		);
	}

	public static ConstructorExpression<ChallengeDetailDtoV2> toChallengeByMember(BooleanExpression exists) {
		return Projections.constructor(
			ChallengeDetailDtoV2.class,
			challenge.id,
			challenge.info,
			challenge.content,
			exists
		);
	}
}
