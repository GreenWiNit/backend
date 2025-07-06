package com.example.green.infra.query.pointproduct;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.dto.PointProductView;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointProductQueryRepositoryImpl implements PointProductQueryRepository {

	private static final int DEFAULT_CURSOR_VIEW_SIZE = 20;
	private final PointProductQueryExecutor pointProductQueryExecutor;

	@Override
	public PageTemplate<PointProductSearchResponse> searchPointProducts(PointProductSearchCondition condition) {
		BooleanExpression expression = PointProductPredicates.fromCondition(condition);

		Long totalCount = pointProductQueryExecutor.countProducts(expression);
		Pagination pagination = Pagination.fromCondition(condition, totalCount);

		List<PointProductSearchResponse> content = pointProductQueryExecutor.findProducts(expression, pagination);

		return PageTemplate.of(content, pagination);
	}

	@Override
	public List<PointProductSearchResponse> searchPointProductsForExcel(PointProductExcelCondition condition) {
		BooleanExpression expression = PointProductPredicates.fromCondition(condition);
		return pointProductQueryExecutor.findProductsForExcel(expression);
	}

	@Override
	public CursorTemplate<Long, PointProductView> getProductsByCursor(Long cursor) {
		BooleanExpression cursorCondition = PointProductPredicates.fromCursorCondition(cursor);
		List<PointProductView> productsView =
			pointProductQueryExecutor.findProductsByCursor(cursorCondition, DEFAULT_CURSOR_VIEW_SIZE);

		if (productsView.isEmpty()) {
			return CursorTemplate.of(productsView);
		}
		return toCursorTemplate(productsView);
	}

	private static CursorTemplate<Long, PointProductView> toCursorTemplate(List<PointProductView> productsView) {
		boolean hasNext = productsView.size() > DEFAULT_CURSOR_VIEW_SIZE;
		if (hasNext) {
			productsView = productsView.subList(0, DEFAULT_CURSOR_VIEW_SIZE);
		}
		Long nextCursor = productsView.getLast().pointProductId();

		return CursorTemplate.ofWithNextCursor(hasNext, nextCursor, productsView);
	}
}
