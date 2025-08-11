package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
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

	public static BooleanExpression activeChallengeCondition(Long cursor, ChallengeStatus status, LocalDateTime now) {
		BooleanExpression expression = personalChallenge.challengeStatus.eq(status)
			.and(personalChallenge.beginDateTime.loe(now))
			.and(personalChallenge.endDateTime.goe(now));
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