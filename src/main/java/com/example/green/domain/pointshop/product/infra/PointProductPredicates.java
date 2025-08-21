package com.example.green.domain.pointshop.product.infra;

import com.example.green.domain.pointshop.product.entity.QPointProduct;
import com.example.green.domain.pointshop.product.entity.vo.DisplayStatus;
import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;
import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
import com.example.green.infra.database.querydsl.QueryPredicates;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointProductPredicates {

	private static final QPointProduct qPointProduct = QPointProduct.pointProduct;

	public static BooleanExpression fromCondition(SellingStatus status, String keyword) {
		return BooleanExpressionConnector.combineWithAnd(
			QueryPredicates.whenNotNull(status, qPointProduct.sellingStatus::eq),
			BooleanExpressionConnector.combineWithOr(
				QueryPredicates.whenNotBlank(keyword, qPointProduct.code.code::containsIgnoreCase),
				QueryPredicates.whenNotBlank(keyword, qPointProduct.basicInfo.name::containsIgnoreCase)
			)
		);
	}

	public static BooleanExpression fromCursorCondition(Long cursor) {
		return BooleanExpressionConnector.combineWithAnd(
			QueryPredicates.whenNotNull(cursor, qPointProduct.id::lt),
			qPointProduct.displayStatus.eq(DisplayStatus.DISPLAY)
		);
	}
}
