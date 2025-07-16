package com.example.green.infra.query.pointproduct;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.product.controller.dto.PointProductSearchResult;
import com.example.green.domain.pointshop.product.controller.dto.PointProductView;
import com.example.green.domain.pointshop.product.entity.QPointProduct;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointProductQueryExecutor {

	private final JPAQueryFactory jpaQueryFactory;
	private final QPointProduct qPointProduct = QPointProduct.pointProduct;

	public List<PointProductSearchResult> findProducts(BooleanExpression expression, Pagination pagination) {
		return jpaQueryFactory
			.select(PointProductProjections.toSearchResponse(qPointProduct))
			.from(qPointProduct)
			.where(expression)
			.orderBy(qPointProduct.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public Long countProducts(BooleanExpression conditions) {
		return jpaQueryFactory
			.select(qPointProduct.count())
			.from(qPointProduct)
			.where(conditions)
			.fetchOne();
	}

	public List<PointProductSearchResult> findProductsForExcel(BooleanExpression expression) {
		return jpaQueryFactory
			.select(PointProductProjections.toSearchResponse(qPointProduct))
			.from(qPointProduct)
			.where(expression)
			.orderBy(qPointProduct.createdDate.desc())
			.fetch();
	}

	public List<PointProductView> findProductsByCursor(BooleanExpression expression, int cursorViewSize) {
		return jpaQueryFactory
			.select(PointProductProjections.toProductsView(qPointProduct))
			.from(qPointProduct)
			.where(expression)
			.orderBy(qPointProduct.id.desc())
			.limit(cursorViewSize)
			.fetch();
	}
}
