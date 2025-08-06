package com.example.green.infra.query.order;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationResult;
import com.example.green.domain.pointshop.order.controller.dto.ExchangeApplicationSearchCondition;
import com.example.green.domain.pointshop.order.controller.dto.PointProductApplicantResult;
import com.example.green.domain.pointshop.order.entity.QOrder;
import com.example.green.domain.pointshop.order.entity.QOrderItem;
import com.example.green.domain.pointshop.order.entity.vo.OrderDeliveryStatus;
import com.example.green.domain.pointshop.order.repository.OrderQueryRepository;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

	private final QOrder qOrder = QOrder.order;
	private final QOrderItem qOrderItem = QOrderItem.orderItem;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public PageTemplate<PointProductApplicantResult> findExchangeApplicantByPointProduct(
		Long pointProductId,
		Integer page,
		Integer size
	) {
		Long totalCount = jpaQueryFactory.select(qOrderItem.count())
			.from(qOrderItem)
			.where(qOrderItem.itemSnapshot.itemId.eq(pointProductId))
			.join(qOrder).on(qOrderItem.order.id.eq(qOrder.id))
			.fetchOne();

		Pagination pagination = Pagination.of(totalCount, page, size);

		List<PointProductApplicantResult> result = jpaQueryFactory.select(Projections.constructor(
				PointProductApplicantResult.class,
				qOrder.memberSnapshot.memberKey,
				qOrder.memberSnapshot.memberEmail,
				qOrder.createdDate,
				qOrder.status
			))
			.from(qOrderItem)
			.where(qOrderItem.itemSnapshot.itemId.eq(pointProductId))
			.join(qOrder).on(qOrderItem.order.id.eq(qOrder.id))
			.orderBy(qOrder.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}

	@Override
	public PageTemplate<ExchangeApplicationResult> searchExchangeApplication(
		ExchangeApplicationSearchCondition condition
	) {
		BooleanExpression expression = fromCondition(condition);
		Long totalCount = jpaQueryFactory.select(qOrder.count())
			.from(qOrder)
			.where(expression)
			.join(qOrderItem).on(qOrder.id.eq(qOrderItem.order.id))
			.fetchOne();

		Pagination pagination = Pagination.fromCondition(condition, totalCount);

		List<ExchangeApplicationResult> result = jpaQueryFactory.select(
				Projections.constructor(ExchangeApplicationResult.class,
					qOrder.id,
					qOrder.createdDate,
					qOrder.memberSnapshot.memberKey,
					qOrder.memberSnapshot.memberEmail,
					qOrderItem.itemSnapshot.itemCode,
					qOrderItem.quantity,
					qOrder.totalPrice,
					qOrder.deliveryAddressSnapshot,
					qOrder.status
				))
			.from(qOrder)
			.where(expression)
			.join(qOrderItem).on(qOrderItem.order.id.eq(qOrder.id))
			.orderBy(qOrder.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<ExchangeApplicationResult> searchExchangeApplicationForExcel(
		ExchangeApplicationSearchCondition condition
	) {
		BooleanExpression expression = fromCondition(condition);
		return jpaQueryFactory.select(
				Projections.constructor(ExchangeApplicationResult.class,
					qOrder.id,
					qOrder.createdDate,
					qOrder.memberSnapshot.memberKey,
					qOrder.memberSnapshot.memberEmail,
					qOrderItem.itemSnapshot.itemCode,
					qOrderItem.quantity,
					qOrder.totalPrice,
					qOrder.deliveryAddressSnapshot,
					qOrder.status
				))
			.from(qOrder)
			.where(expression)
			.join(qOrderItem).on(qOrderItem.order.id.eq(qOrder.id))
			.orderBy(qOrder.createdDate.desc())
			.fetch();
	}

	private BooleanExpression fromCondition(ExchangeApplicationSearchCondition condition) {
		return combineConditions(
			statusEq(condition.status()),
			keywordContains(condition.keyword())
		);
	}

	private BooleanExpression combineConditions(BooleanExpression... expressions) {
		return Arrays.stream(expressions)
			.filter(Objects::nonNull)
			.reduce(BooleanExpression::and)
			.orElse(null);
	}

	private BooleanExpression statusEq(OrderDeliveryStatus status) {
		if (status == null) {
			return null;
		}
		return qOrder.status.eq(status);
	}

	private BooleanExpression keywordContains(String keyword) {
		if (keyword == null) {
			return null;
		}
		return qOrderItem.itemSnapshot.itemCode.containsIgnoreCase(keyword);
	}
}
