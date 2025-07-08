package com.example.green.infra.query.order;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.order.QOrder;
import com.example.green.domain.pointshop.entity.order.QOrderItem;
import com.example.green.domain.pointshop.repository.OrderQueryRepository;
import com.example.green.domain.pointshop.repository.dto.PointProductApplicantResult;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.Projections;
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
				qOrder.memberSnapshot.memberCode,
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
}
