package com.example.green.infra.query.pointproduct;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.product.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.domain.pointshop.product.repository.PointProductQueryRepository;
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
	public PageTemplate<PointProductSearchResult> searchPointProducts(PointProductSearchCondition condition) {
		BooleanExpression expression = PointProductPredicates.fromCondition(condition);

		Long totalCount = pointProductQueryExecutor.countProducts(expression);
		Pagination pagination = Pagination.fromCondition(condition, totalCount);

		List<PointProductSearchResult> content = pointProductQueryExecutor.findProducts(expression, pagination);

		return PageTemplate.of(content, pagination);
	}

	@Override
	public List<PointProductSearchResult> searchPointProductsForExcel(PointProductExcelCondition condition) {
		BooleanExpression expression = PointProductPredicates.fromCondition(condition);
		return pointProductQueryExecutor.findProductsForExcel(expression);
	}

	@Override
	public CursorTemplate<Long, PointProductView> getProductsByCursor(Long cursor) {
		BooleanExpression cursorCondition = PointProductPredicates.fromCursorCondition(cursor);
		List<PointProductView> productsView =
			pointProductQueryExecutor.findProductsByCursor(cursorCondition, DEFAULT_CURSOR_VIEW_SIZE);

		boolean hasNext = productsView.size() > DEFAULT_CURSOR_VIEW_SIZE;
		if (!hasNext) {
			return CursorTemplate.of(productsView);
		}

		productsView.removeLast();
		if (productsView.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		return CursorTemplate.ofWithNextCursor(productsView.getLast().pointProductId(), productsView);
	}

}
