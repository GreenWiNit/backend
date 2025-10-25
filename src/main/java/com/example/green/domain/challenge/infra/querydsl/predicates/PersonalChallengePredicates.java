package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonalChallengePredicates {

	public static BooleanExpression myParticipationCondition(Long memberId, Long cursor, LocalDateTime now) {
		return BooleanExpressionConnector.combineWithAnd(
			activeChallengeCondition(cursor, now),
			personalChallengeParticipation.memberId.eq(memberId)
		);
	}

	public static BooleanExpression activeChallengeCondition(Long cursor, LocalDateTime now) {
		return BooleanExpressionConnector.combineWithAnd(
			personalChallenge.beginDate.loe(now.toLocalDate()),
			personalChallenge.endDate.goe(now.toLocalDate()),
			personalChallenge.displayStatus.eq(ChallengeDisplay.VISIBLE),
			getCursorCondition(cursor)
		);
	}

	public static BooleanExpression memberParticipationExists(Long challengeId, Long memberId) {
		return JPAExpressions.selectOne()
			.from(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.personalChallenge.id.eq(challengeId),
				personalChallengeParticipation.memberId.eq(memberId)
			).exists();
	}

	private static BooleanExpression getCursorCondition(Long cursor) {
		if (cursor == null || cursor <= 0) {
			return null;
		}
		return personalChallenge.id.lt(cursor);
	}
}