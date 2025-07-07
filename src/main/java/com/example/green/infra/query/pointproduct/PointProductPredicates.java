package com.example.green.infra.query.pointproduct;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.util.StringUtils;

import com.example.green.domain.pointshop.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.entity.pointproduct.QPointProduct;
import com.example.green.domain.pointshop.entity.pointproduct.vo.DisplayStatus;
import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;
import com.example.green.domain.pointshop.exception.PointProductException;
import com.example.green.domain.pointshop.exception.PointProductExceptionMessage;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointProductPredicates {

	private static final QPointProduct qPointProduct = QPointProduct.pointProduct;

	public static BooleanExpression fromCondition(PointProductSearchCondition condition) {
		return combineConditions(
			filterByStatus(condition.status()),
			filterByKeyword(condition.keyword())
		);
	}

	public static BooleanExpression fromCondition(PointProductExcelCondition condition) {
		return combineConditions(
			filterByStatus(condition.status()),
			filterByKeyword(condition.keyword())
		);
	}

	public static BooleanExpression fromCursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return qPointProduct.id.lt(cursor)
			.and(qPointProduct.displayStatus.eq(DisplayStatus.DISPLAY));
	}

	public static BooleanExpression filterByStatus(SellingStatus status) {
		if (status == null) {
			return null;
		}
		return qPointProduct.sellingStatus.eq(status);
	}

	public static BooleanExpression filterByKeyword(String keyword) {
		if (!StringUtils.hasText(keyword)) {
			return null;
		}

		String trimmedKeyword = keyword.trim();
		validateKeywordLength(trimmedKeyword);

		return qPointProduct.code.code.containsIgnoreCase(trimmedKeyword)
			.or(qPointProduct.basicInfo.name.containsIgnoreCase(trimmedKeyword));
	}

	private static BooleanExpression combineConditions(BooleanExpression... expressions) {
		return Arrays.stream(expressions)
			.filter(Objects::nonNull)
			.reduce(BooleanExpression::and)
			.orElse(null);
	}

	private static void validateKeywordLength(String trimmedKeyword) {
		if (trimmedKeyword.length() < 2) {
			throw new PointProductException(PointProductExceptionMessage.INVALID_SEARCH_KEYWORD);
		}
	}
}
