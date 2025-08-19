package com.example.green.infra.database.querydsl;

import java.util.Arrays;
import java.util.Objects;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BooleanExpressionConnector {

	public static BooleanExpression combineWithAnd(BooleanExpression... expressions) {
		return Arrays.stream(expressions)
			.filter(Objects::nonNull)
			.reduce(BooleanExpression::and)
			.orElse(null);
	}

	public static BooleanExpression combineWithOr(BooleanExpression... expressions) {
		return Arrays.stream(expressions)
			.filter(Objects::nonNull)
			.reduce(BooleanExpression::or)
			.orElse(null);
	}
}
