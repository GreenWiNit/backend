package com.example.green.domain.point.infra.querydsl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointTransactionQueryExecutor {

	private final QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;
	private final JPAQueryFactory jpaQueryFactory;
	private final EntityManager entityManager;

	public List<MyPointTransactionDto> createMyPointTransactionQuery(BooleanExpression expression, int limit) {
		return jpaQueryFactory
			.select(PointTransactionProjections.toMyPointTransaction(qPointTransaction))
			.from(qPointTransaction)
			.where(expression)
			.orderBy(qPointTransaction.id.desc())
			.limit(limit + 1)
			.fetch();
	}

	public Map<Long, BigDecimal> createEarnedPointQuery(BooleanExpression expression) {
		return jpaQueryFactory
			.select(qPointTransaction.memberId, qPointTransaction.balanceAfter.amount)
			.from(qPointTransaction)
			.where(expression)
			.transform(GroupBy.groupBy(qPointTransaction.memberId)
				.as(qPointTransaction.balanceAfter.amount));
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
			.select(PointTransactionProjections.toPointTransactions(qPointTransaction))
			.from(qPointTransaction)
			.where(expression)
			.orderBy(qPointTransaction.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<PointTransactionDto> createPointTransactionForExcelQuery(Long memberId) {
		return jpaQueryFactory
			.select(PointTransactionProjections.toPointTransactions(qPointTransaction))
			.from(qPointTransaction)
			.where(qPointTransaction.memberId.eq(memberId))
			.orderBy(qPointTransaction.id.desc())
			.fetch();
	}

	public TypedQuery<MemberPointSummary> toFindMemberPointSummaryQuery() {
		return entityManager.createQuery("""
			SELECT new com.example.green.domain.point.repository.dto.MemberPointSummary(
				COALESCE(
					(SELECT pt2.balanceAfter.amount
					 FROM PointTransaction pt2
					 WHERE pt2.memberId = :memberId
					 ORDER BY pt2.id DESC
					 LIMIT 1), 0
				),
				COALESCE(SUM(CASE WHEN pt.type = 'EARN' THEN pt.pointAmount.amount ELSE 0 END), 0),
				COALESCE(SUM(CASE WHEN pt.type = 'SPEND' THEN pt.pointAmount.amount ELSE 0 END), 0)
			)
			FROM PointTransaction pt
			WHERE pt.memberId = :memberId
			""", MemberPointSummary.class);
	}
}

