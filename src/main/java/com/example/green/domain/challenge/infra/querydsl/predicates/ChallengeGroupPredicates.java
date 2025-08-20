package com.example.green.domain.challenge.infra.querydsl.predicates;

import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

	private static BooleanExpression fromCondition(String cursor) {
		if (cursor == null || cursor.isBlank()) {
			return null;
		}

		String[] parts = cursor.split(",", 2); // limit to 2 parts
		if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
			return null;
		}

		return parseCursorExpression(parts);
	}

	private static BooleanExpression parseCursorExpression(String[] parts) {
		try {
			LocalDateTime dateCursor = LocalDateTime.parse(parts[0]);
			long idCursor = Long.parseLong(parts[1]);
			return challengeGroup.createdDate.lt(dateCursor)
				.or(challengeGroup.createdDate.eq(dateCursor)
					.and(challengeGroup.id.lt(idCursor)));
		} catch (DateTimeParseException | NumberFormatException e) {
			return null;
		}
	}
}
