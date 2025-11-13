package com.example.green.domain.dashboard.growth.infra;

import java.util.List;
import java.util.Optional;

import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.entity.QPlantGrowthItem;
import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlantGrowthItemRepositoryImpl implements PlantGrowthItemRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetPlantGrowthItemResponse> findItemsByMemberId(Long memberId) {
		QPlantGrowthItem item = QPlantGrowthItem.plantGrowthItem;

		return queryFactory
			.select(Projections.constructor(GetPlantGrowthItemResponse.class,
				item.itemName,
				item.itemImgUrl,
				item.applicability))
			.from(item)
			.where(item.memberId.eq(memberId))
			.fetch();
	}

	@Override
	public Optional<PlantGrowthItem> findItemByIdAndMemberId(Long memberId, Long itemId) {
		QPlantGrowthItem plantGrowthItem = QPlantGrowthItem.plantGrowthItem;

		PlantGrowthItem item = queryFactory
			.selectFrom(plantGrowthItem)
			.where(
				plantGrowthItem.id.eq(itemId),
				plantGrowthItem.memberId.eq(memberId)
			)
			.fetchOne();
		return Optional.ofNullable(item);
	}
}
