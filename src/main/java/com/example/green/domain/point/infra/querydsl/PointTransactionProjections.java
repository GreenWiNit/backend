package com.example.green.domain.point.infra.querydsl;

import java.math.BigDecimal;

import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PointTransactionProjections {

	public static ConstructorExpression<MyPointTransactionDto> toMyPointTransaction(
		QPointTransaction qPointTransaction) {
		return Projections.constructor(MyPointTransactionDto.class,
			qPointTransaction.id,
			qPointTransaction.pointSource.description,
			qPointTransaction.pointAmount.amount,
			qPointTransaction.type,
			qPointTransaction.createdDate
		);
	}

	public static ConstructorExpression<PointTransactionDto> toPointTransactions(QPointTransaction qPointTransaction) {
		return Projections.constructor(
			PointTransactionDto.class,
			qPointTransaction.id,
			qPointTransaction.pointSource.targetType,
			qPointTransaction.pointSource.description,
			createEarnedAmountCase(qPointTransaction),
			createSpendAmountCase(qPointTransaction),
			qPointTransaction.balanceAfter.amount,
			qPointTransaction.createdDate
		);
	}

	private static NumberExpression<BigDecimal> createEarnedAmountCase(QPointTransaction qPointTransaction) {
		return new CaseBuilder()
			.when(qPointTransaction.type.eq(TransactionType.EARN))
			.then(qPointTransaction.pointAmount.amount)
			.otherwise(BigDecimal.ZERO).as("earnedAmount");
	}

	private static NumberExpression<BigDecimal> createSpendAmountCase(QPointTransaction qPointTransaction) {
		return new CaseBuilder()
			.when(qPointTransaction.type.eq(TransactionType.SPEND))
			.then(qPointTransaction.pointAmount.amount)
			.otherwise(BigDecimal.ZERO).as("spentAmount");
	}
}
