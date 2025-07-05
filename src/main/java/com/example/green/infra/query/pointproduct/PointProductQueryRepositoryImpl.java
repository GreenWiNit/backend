package com.example.green.infra.query.pointproduct;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.pointshop.controller.dto.PointProductExcelCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchCondition;
import com.example.green.domain.pointshop.controller.dto.PointProductSearchResponse;
import com.example.green.domain.pointshop.controller.query.PointProductQueryRepository;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PointProductQueryRepositoryImpl implements PointProductQueryRepository {

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
}
