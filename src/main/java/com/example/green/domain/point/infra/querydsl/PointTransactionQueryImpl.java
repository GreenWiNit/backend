package com.example.green.domain.point.infra.querydsl;

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

import lombok.RequiredArgsConstructor;

// todo: 통합 테스트 추가
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointTransactionQueryImpl implements PointTransactionQueryRepository {

	private static final int DEFAULT_CURSOR_VIEW_SIZE = 20;
	private static final QPointTransaction qPointTransaction = QPointTransaction.pointTransaction;
	private final PointTransactionQueryExecutor queryExecutor;

	@Override
	public MemberPointSummary findMemberPointSummary(Long memberId) {
		return queryExecutor.toFindMemberPointSummaryQuery()
			.setParameter("memberId", memberId)
			.getSingleResult();
	}

	@Override
	public CursorTemplate<Long, MyPointTransactionDto> getPointTransaction(
		Long memberId,
		Long cursor,
		TransactionType status
	) {
		BooleanExpression expression = PointTransactionPredicates.toPointTransactionQuery(memberId, cursor, status);
		List<MyPointTransactionDto> content =
			queryExecutor.createMyPointTransactionQuery(expression, DEFAULT_CURSOR_VIEW_SIZE);

		return CursorTemplate.from(content, DEFAULT_CURSOR_VIEW_SIZE, MyPointTransactionDto::pointTransactionId);
	}

	@Override
	public Map<Long, BigDecimal> findEarnedPointByMember(List<Long> memberIds) {
		BooleanExpression finalExpression = PointTransactionPredicates.toEarnedPointByMemberQuery(memberIds);
		Map<Long, BigDecimal> earnedPoints = queryExecutor.createEarnedPointQuery(finalExpression);

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

	@Override
	public List<PointTransactionDto> findPointTransactionByMemberForExcel(Long memberId) {
		return queryExecutor.createPointTransactionForExcelQuery(memberId);
	}
}

