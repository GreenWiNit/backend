package com.example.green.domain.pointshop.item.infra;

import org.springframework.stereotype.Repository;

import com.example.green.domain.pointshop.item.entity.QOrderPointItem;
import com.example.green.domain.pointshop.item.repository.PointItemOrderQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PointItemOrderRepositoryImpl implements PointItemOrderQueryRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByMemberIdAndPointItemId(Long memberId, Long pointItemId) {
		QOrderPointItem order = QOrderPointItem.orderPointItem;

		return queryFactory
			.selectOne()
			.from(order)
			.where(
				order.memberSnapshot.memberId.eq(memberId)
					.and(order.pointItemSnapshot.pointItemId.eq(pointItemId))
			)
			.fetchFirst() != null;
	}
}
