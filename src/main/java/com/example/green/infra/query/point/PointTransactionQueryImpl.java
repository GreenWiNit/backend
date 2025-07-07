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
import com.querydsl.core.types.dsl.BooleanExpression;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

// todo: 통합 테스트 추가
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointTransactionQueryImpl implements PointTransactionQueryRepository {

	private final static int DEFAULT_CURSOR_VIEW_SIZE = 20;
	private final static QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;
	private final PointTransactionQueryExecutor queryExecutor;
	private final EntityManager entityManager;

	@Override
	public MemberPointSummary findMemberPointSummary(Long memberId) {
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
		BooleanExpression expression = PointTransactionPredicates.fromCondition(memberId, cursor, status);
		List<MyPointTransactionDto> content =
			queryExecutor.createMyPointTransactionQuery(expression, DEFAULT_CURSOR_VIEW_SIZE);

		boolean hasNext = content.size() > DEFAULT_CURSOR_VIEW_SIZE;
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
		BooleanExpression expression = PointTransactionPredicates.fromCondition(memberIds);
		Map<Long, BigDecimal> earnedPoints = queryExecutor.createEarnedPointQuery(expression);

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
		BooleanExpression expression = qPointTransaction.memberId.eq(memberId);

		Long totalCount = queryExecutor.createTotalCountQuery(expression);
		Pagination pagination = Pagination.fromCondition(condition, totalCount);
		List<PointTransactionDto> fetch = queryExecutor.createPointTransactionsQuery(expression, pagination);

		return PageTemplate.of(fetch, pagination);
	}
}

