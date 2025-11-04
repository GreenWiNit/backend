package com.example.green.domain.pointshop.item.infra;

import com.example.green.domain.pointshop.item.entity.QPointItem;
import com.example.green.domain.pointshop.item.entity.vo.ItemDisplayStatus;
import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
import com.example.green.infra.database.querydsl.QueryPredicates;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointItemPredicates {

	private static final QPointItem qPointItem = QPointItem.pointItem;

	public static BooleanExpression fromCondition(String keyword) {
		return BooleanExpressionConnector.combineWithAnd(
			BooleanExpressionConnector.combineWithOr(
				QueryPredicates.whenNotBlank(keyword, qPointItem.itemCode.code::containsIgnoreCase),
				QueryPredicates.whenNotBlank(keyword, qPointItem.itemBasicInfo.itemName::containsIgnoreCase)
			)
		);
	}

	public static BooleanExpression fromCursorCondition(Long cursor) {
		return BooleanExpressionConnector.combineWithAnd(
			QueryPredicates.whenNotNull(cursor, qPointItem.id::lt),
			qPointItem.displayStatus.eq(ItemDisplayStatus.DISPLAY)
		);
	}
}
