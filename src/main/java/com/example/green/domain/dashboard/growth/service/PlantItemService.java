package com.example.green.domain.dashboard.growth.service;

import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.dashboard.growth.dto.request.ChangePositionRequest;
import com.example.green.domain.dashboard.growth.dto.response.ChangePositionGrowthItemResponse;
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
		int retry = 3;
		while (retry > 0) {
			try {
				PlantGrowthItem growthItem = plantGrowthItemRepository.findItemByIdAndMemberId(memberId, itemId)
					.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_ITEM));
				growthItem.apply();
				return;
			} catch (ObjectOptimisticLockingFailureException e) {
				retry--;
				if (retry == 0) {
					throw new GrowthException(GrowthExceptionMessage.RETRY_AGAIN);
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	@Transactional
	public ChangePositionGrowthItemResponse changePositionGrowthItem(Long memberId, Long itemId,
		ChangePositionRequest request) {
		PlantGrowthItem growthItem = plantGrowthItemRepository.findItemByIdAndMemberId(memberId, itemId)
			.orElseThrow(() -> new GrowthException(GrowthExceptionMessage.NOT_FOUND_ITEM));

		if (!growthItem.isApplicability()) {
			throw new GrowthException(GrowthExceptionMessage.NOT_SETTING_APPLICABILITY);
		}

		growthItem.changePosition(request.positionX(), request.positionY());

		return new ChangePositionGrowthItemResponse(
			growthItem.getItemName(),
			growthItem.getItemImgUrl(),
			growthItem.isApplicability(),
			growthItem.getPositionX(),
			growthItem.getPositionY()
		);
	}
}
