package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;

import java.time.LocalDateTime;
import java.util.List;

import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChallengeGroupPredicates {

	public static BooleanExpression getMyGroupCondition(Long challengeId, List<Long> groupIds, String cursor) {
		return BooleanExpressionConnector.combineWithAnd(
			challengeGroup.teamChallengeId.eq(challengeId),
			challengeGroup.id.in(groupIds),
			fromCondition(cursor)
		);
	}

	public static BooleanExpression getGroupCursorCondition(Long challengeId, String cursor, Long memberId) {
		return BooleanExpressionConnector.combineWithAnd(
			challengeGroup.teamChallengeId.eq(challengeId),
			challengeGroup.leaderId.ne(memberId),
			fromCondition(cursor));
	}

	public static BooleanExpression fromCondition(String cursor) {
		if (cursor == null || cursor.isBlank()) {
			return null;
		}

		String[] parts = cursor.split(",");
		LocalDateTime dateCursor = LocalDateTime.parse(parts[0]);
		Long idCursor = Long.parseLong(parts[1]);
		return challengeGroup.createdDate.lt(dateCursor)
			.or(challengeGroup.createdDate.eq(dateCursor)
				.and(challengeGroup.id.lt(idCursor)));
	}
}
