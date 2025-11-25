package com.example.green.domain.dashboard.growth.repository;

import java.util.List;
import java.util.Optional;

import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;

public interface PlantGrowthItemRepositoryCustom {
	List<GetPlantGrowthItemResponse> findItemsByMemberId(Long memberId);

	Optional<PlantGrowthItem> findByIdAndMember_Id(Long itemId, Long memberId);
}
