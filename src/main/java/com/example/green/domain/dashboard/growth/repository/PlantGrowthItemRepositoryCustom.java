package com.example.green.domain.dashboard.growth.repository;

import java.util.List;

import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;

public interface PlantGrowthItemRepositoryCustom {
	List<GetPlantGrowthItemResponse> findItemsByMemberId(Long memberId);
}
