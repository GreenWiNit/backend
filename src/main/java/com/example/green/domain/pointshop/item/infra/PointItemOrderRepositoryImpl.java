package com.example.green.domain.pointshop.item.infra;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.pointshop.item.dto.response.BuyerInformation;
import com.example.green.domain.pointshop.item.dto.response.ItemWithApplicantsDTO;
import com.example.green.domain.pointshop.item.entity.PointItem;
import com.example.green.domain.pointshop.item.entity.QOrderPointItem;
import com.example.green.domain.pointshop.item.entity.QPointItem;
import com.example.green.domain.pointshop.item.repository.PointItemOrderQueryRepository;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.Projections;
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

	//관리자 전체 아이템 조회 - 구매 사용자 조회
	@Override
	public PageTemplate<ItemWithApplicantsDTO> findAllItemsWithApplicants(Integer page, Integer size) {
		QPointItem pointItem = QPointItem.pointItem;
		QOrderPointItem order = QOrderPointItem.orderPointItem;

		int currentPage = page != null ? page : 0;
		int pageSize = size != null ? size : 20;
		int offset = currentPage * pageSize;
		int limit = size != null ? size : 20;

		List<PointItem> items = queryFactory
			.selectFrom(pointItem)
			.offset(offset)
			.limit(limit)
			.fetch();

		List<ItemWithApplicantsDTO> result = items.stream().map(item -> {
			// 구매자 정보 조회
			List<BuyerInformation> buyers = queryFactory
				.select(Projections.constructor(BuyerInformation.class,
					order.memberSnapshot.memberKey,
					order.memberSnapshot.memberEmail
				))
				.from(order)
				.where(order.pointItemSnapshot.pointItemId.eq(item.getId()))
				.fetch();

			return new ItemWithApplicantsDTO(
				item.getId(),
				item.getItemBasicInfo().getItemName(),
				item.getItemMedia().getItemThumbNailUrl(),
				buyers
			);
		}).toList();

		long totalCount = queryFactory
			.selectFrom(pointItem)
			.fetchCount();

		Pagination pagination = Pagination.of(totalCount, currentPage, pageSize);

		return PageTemplate.of(result, pagination);
	}
}
