package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;

import java.time.LocalDateTime;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChallengePredicates {

	public static BooleanExpression myParticipationCondition(Long memberId, Long cursor) {
		BooleanExpression expression = personalChallengeParticipation.memberId.eq(memberId);
		if (cursor == null) {
			return expression;
		}
		return expression.and(personalChallenge.id.lt(cursor));
	}

	public static BooleanExpression activeChallengeCondition(Long cursor, LocalDateTime now) {
		BooleanExpression expression = personalChallenge.beginDate.loe(now.toLocalDate())
			.and(personalChallenge.endDate.goe(now.toLocalDate()));
		if (cursor == null) {
			return expression;
		}
		return expression.and(personalChallenge.id.lt(cursor));
	}

	public static BooleanExpression memberParticipationExists(Long challengeId, Long memberId) {
		return JPAExpressions.selectOne()
			.from(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.personalChallenge.id.eq(challengeId),
				personalChallengeParticipation.memberId.eq(memberId)
			).exists();
	}
}