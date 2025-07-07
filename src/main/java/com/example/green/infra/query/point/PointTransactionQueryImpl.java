package com.example.green.infra.query.point;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.point.controller.dto.PointTransactionSearchCondition;
import com.example.green.domain.point.entity.QPointTransaction;
import com.example.green.domain.point.entity.vo.TransactionType;
import com.example.green.domain.point.repository.PointTransactionQueryRepository;
import com.example.green.domain.point.repository.dto.MemberPointSummary;
import com.example.green.domain.point.repository.dto.MyPointTransactionDto;
import com.example.green.domain.point.repository.dto.PointTransactionDto;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
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
	public CursorTemplate<Long, MyPointTransactionDto> getPointTransaction(
		Long memberId,
		Long cursor,
		TransactionType status
	) {
		BooleanExpression expression = getExpression(memberId, cursor, status);
		List<MyPointTransactionDto> content = getExecution(expression);
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

	@Override
	public Map<Long, BigDecimal> findEarnedPointByMember(List<Long> memberIds) {
		Map<Long, BigDecimal> earnedPoints = queryFactory
			.select(
				qPointTransaction.memberId,
				qPointTransaction.pointAmount.amount.sum()
			)
			.from(qPointTransaction)
			.where(
				qPointTransaction.type.eq(TransactionType.EARN),
				qPointTransaction.memberId.in(memberIds)
			)
			.groupBy(qPointTransaction.memberId)
			.transform(GroupBy.groupBy(qPointTransaction.memberId)
				.as(qPointTransaction.pointAmount.amount.sum()));

		return memberIds.stream()
			.collect(Collectors.toMap(
				Function.identity(),
				memberId -> earnedPoints.getOrDefault(memberId, BigDecimal.ZERO)
			));
	}

	@Override
	public PageTemplate<PointTransactionDto> findPointTransactionByMember(
		Long memberId,
		PointTransactionSearchCondition condition
	) {
		Long totalCount = queryFactory.select(qPointTransaction.count())
			.from(qPointTransaction)
			.where(qPointTransaction.memberId.eq(memberId))
			.fetchOne();
		Pagination pagination = Pagination.fromCondition(condition, totalCount);
		List<PointTransactionDto> fetch = queryFactory
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
			.where(qPointTransaction.memberId.eq(memberId))
			.orderBy(qPointTransaction.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(fetch, pagination);
	}

	private List<MyPointTransactionDto> getExecution(BooleanExpression expression) {
		return queryFactory
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
