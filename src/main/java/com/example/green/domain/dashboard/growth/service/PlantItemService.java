package com.example.green.domain.dashboard.growth.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlantItemService {

	private final PlantGrowthItemRepository plantGrowthItemRepository;

	public List<GetPlantGrowthItemResponse> getPlantGrowthItems(Long memberId) {
		List<GetPlantGrowthItemResponse> getItems = plantGrowthItemRepository.findItemsByMemberId(memberId);
		if (getItems.isEmpty()) {
			return List.of();
		}
		return getItems;
	}

	@Transactional
	public void changeApplicability(Long memberId, Long itemId) {
		PlantGrowthItem growthItem = plantGrowthItemRepository.findItemByIdAndMemberId(memberId, itemId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_ITEM));
		growthItem.apply();
	}

}
