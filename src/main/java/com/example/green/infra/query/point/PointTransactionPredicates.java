package com.example.green.infra.query.point;

import java.util.List;

import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointTransactionPredicates {

	private static final QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;

	public static BooleanExpression fromCondition(Long memberId, Long cursor, TransactionType status) {
		BooleanExpression expression = qPointTransaction.memberId.eq(memberId);
		if (cursor != null) {
			expression = expression.and(qPointTransaction.id.lt(cursor));
		}
		if (status != null) {
			expression = expression.and(qPointTransaction.type.eq(status));
		}
		return expression;
	}

	public static BooleanExpression fromCondition(List<Long> memberIds) {
		return qPointTransaction.type.eq(TransactionType.EARN).and(
			qPointTransaction.memberId.in(memberIds)
		);
	}
}
