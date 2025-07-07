package com.example.green.infra.query.point;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionQueryExecutor {

	private final QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;
	private final JPAQueryFactory jpaQueryFactory;

	public List<MyPointTransactionDto> createMyPointTransactionQuery(BooleanExpression expression, int limit) {
		return jpaQueryFactory
			.select(Projections.constructor(MyPointTransactionDto.class,
				qPointTransaction.id,
				qPointTransaction.pointSource.description,
				qPointTransaction.pointAmount.amount,
				qPointTransaction.type,
				qPointTransaction.createdDate
			))
			.from(qPointTransaction)
			.where(expression)
			.orderBy(qPointTransaction.id.desc())
			.limit(limit + 1)
			.fetch();
	}

	public Map<Long, BigDecimal> createEarnedPointQuery(BooleanExpression expression) {
		return jpaQueryFactory
			.select(
				qPointTransaction.memberId,
				qPointTransaction.pointAmount.amount.sum()
			)
			.from(qPointTransaction)
			.where(
			)
			.groupBy(qPointTransaction.memberId)
			.transform(GroupBy.groupBy(qPointTransaction.memberId)
				.as(qPointTransaction.pointAmount.amount.sum()));
	}

	public Long createTotalCountQuery(BooleanExpression expression) {
		return jpaQueryFactory.select(qPointTransaction.count())
			.from(qPointTransaction)
			.where(expression)
			.fetchOne();
	}

	public List<PointTransactionDto> createPointTransactionsQuery(
		BooleanExpression expression,
		Pagination pagination
	) {
		return jpaQueryFactory
			.select(Projections.constructor(
				PointTransactionDto.class,
				qPointTransaction.id,
				qPointTransaction.pointSource.targetType,
				qPointTransaction.pointSource.description,
				new CaseBuilder()
					.when(qPointTransaction.type.eq(TransactionType.EARN))
					.then(qPointTransaction.pointAmount.amount)
					.otherwise(BigDecimal.ZERO).as("earnedAmount"),
				new CaseBuilder()
					.when(qPointTransaction.type.eq(TransactionType.SPEND))
					.then(qPointTransaction.pointAmount.amount)
					.otherwise(BigDecimal.ZERO).as("spentAmount"),
				qPointTransaction.balanceAfter.amount,
				qPointTransaction.createdDate
			))
			.from(qPointTransaction)
			.where(expression)
			.orderBy(qPointTransaction.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

}
