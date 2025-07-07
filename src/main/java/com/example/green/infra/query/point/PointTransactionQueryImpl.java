package com.example.green.infra.query.point;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransaction;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

// todo: 통합 테스트 추가
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointTransactionQueryImpl implements PointTransactionQueryRepository {

	private final static int DEFAULT_PAGE_SIZE = 20;
	private final static QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;
	private final EntityManager entityManager;
	private final JPAQueryFactory queryFactory;

	@Override
	public MemberPointSummary findMemberPointSummary(Long memberId) {
		return entityManager.createQuery("""
				SELECT new com.example.green.domain.point.controller.dto.MemberPointSummary(
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
				""", MemberPointSummary.class)
			.setParameter("memberId", memberId)
			.getSingleResult();
	}

	@Override
	public CursorTemplate<Long, MyPointTransaction> getPointTransaction(
		Long memberId,
		Long cursor,
		TransactionType status
	) {
		BooleanExpression expression = getExpression(memberId, cursor, status);
		List<MyPointTransaction> content = getExecution(expression);
		boolean hasNext = content.size() > DEFAULT_PAGE_SIZE;
		if (!hasNext) {
			return CursorTemplate.of(content);
		}
		content.removeLast();

		if (content.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}
		return CursorTemplate.ofWithNextCursor(content.getLast().pointTransactionId(), content);
	}

	private List<MyPointTransaction> getExecution(BooleanExpression expression) {
		return queryFactory
			.select(Projections.constructor(MyPointTransaction.class,
				qPointTransaction.id,
				qPointTransaction.pointSource.detail,
				qPointTransaction.pointAmount.amount,
				qPointTransaction.type,
				qPointTransaction.createdDate
			))
			.from(qPointTransaction)
			.where(expression)
			.orderBy(qPointTransaction.id.desc())
			.limit(DEFAULT_PAGE_SIZE + 1)
			.fetch();
	}

	private static BooleanExpression getExpression(Long memberId, Long cursor, TransactionType status) {
		BooleanExpression expression = qPointTransaction.memberId.eq(memberId);
		if (cursor != null) {
			expression = expression.and(qPointTransaction.id.lt(cursor));
		}
		if (status != null) {
			expression = expression.and(qPointTransaction.type.eq(status));
		}
		return expression;
	}

}
