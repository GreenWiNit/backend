package com.example.green.domain.pointshop.item.infra;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.pointshop.item.dto.response.PointItemResponse;
import com.example.green.domain.pointshop.item.dto.response.PointItemSearchResponse;
import com.example.green.domain.pointshop.item.entity.QPointItem;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PointItemQueryExecutor {

	private final JPAQueryFactory jpaQueryFactory;
	private final QPointItem qPointItem = QPointItem.pointItem;

	public List<PointItemSearchResponse> findItems(BooleanExpression expression, Pagination pagination) {
		return jpaQueryFactory
			.select(PointItemProjections.toSearchItemResponse(qPointItem))
			.from(qPointItem)
			.where(expression)
			.orderBy(qPointItem.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public Long countItems(BooleanExpression conditions) {
		return jpaQueryFactory
			.select(qPointItem.count())
			.from(qPointItem)
			.where(conditions)
			.fetchOne();
	}

	public List<PointItemSearchResponse> findItemsForExcel(BooleanExpression expression) {
		return jpaQueryFactory
			.select(PointItemProjections.toSearchItemResponse(qPointItem))
			.from(qPointItem)
			.where(expression)
			.orderBy(qPointItem.createdDate.desc())
			.fetch();
	}

	public List<PointItemResponse> findItemByCursor(BooleanExpression expression, int cursorViewSize) {
		return jpaQueryFactory
			.select(PointItemProjections.toPointItemView(qPointItem))
			.from(qPointItem)
			.where(expression)
			.orderBy(qPointItem.id.desc())
			.limit(cursorViewSize + 1)
			.fetch();
	}

}
