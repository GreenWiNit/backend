package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.challenge.QChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;
import static com.example.green.infra.database.querydsl.BooleanExpressionConnector.*;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengePredicates {

	public static BooleanExpression myParticipationCondition(Long memberId, Long cursor, ChallengeType type) {
		return combineWithAnd(
			getParticipationCursorCondition(cursor),
			participation.memberId.eq(memberId),
			challenge.display.eq(ChallengeDisplay.VISIBLE),
			challenge.type.eq(type)
		);
	}

	public static BooleanExpression activeChallengeCondition(Long cursor, ChallengeType type) {
		return combineWithAnd(
			challenge.display.eq(ChallengeDisplay.VISIBLE),
			getChallengeCursorCondition(cursor),
			challenge.type.eq(type)
		);
	}

	public static BooleanExpression memberParticipationExists(Long challengeId, Long memberId) {
		return JPAExpressions.selectOne()
			.from(participation)
			.where(
				participation.challenge.id.eq(challengeId),
				participation.memberId.eq(memberId)
			).exists();
	}

	private static BooleanExpression getChallengeCursorCondition(Long cursor) {
		if (cursor == null || cursor <= 0) {
			return null;
		}
		return challenge.id.lt(cursor);
	}

	private static BooleanExpression getParticipationCursorCondition(Long cursor) {
		if (cursor == null || cursor <= 0) {
			return null;
		}
		return participation.id.lt(cursor);
	}
}