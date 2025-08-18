package com.example.green.infra.database.querydsl;

import java.util.function.Function;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryPredicates {

	public static BooleanExpression whenNotBlank(String value, Function<String, BooleanExpression> predicate) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return predicate.apply(value);
	}

	public static <T> BooleanExpression whenNotNull(T value, Function<T, BooleanExpression> predicate) {
		if (value == null) {
			return null;
		}
		return predicate.apply(value);
	}
}