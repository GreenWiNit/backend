package com.example.green.domain.dashboard.growth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.green.domain.dashboard.growth.dto.request.ChangePositionRequest;
import com.example.green.domain.dashboard.growth.dto.response.ChangePositionGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.entity.PlantGrowthItem;
import com.example.green.domain.dashboard.growth.exception.GrowthException;
import com.example.green.domain.dashboard.growth.message.GrowthExceptionMessage;
import com.example.green.domain.dashboard.growth.repository.PlantGrowthItemRepository;

class PlantItemServiceTest {

	@Mock
	private PlantGrowthItemRepository plantGrowthItemRepository;

	@InjectMocks
	private PlantItemService plantItemService;

	private PlantGrowthItem plantGrowthItem;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);

		plantGrowthItem = PlantGrowthItem.create(
			1L,
			"맑은 뭉게 구름",
			"https://my-plant-growth-bucket.s3.ap-northeast-2.amazonaws.com/images/sunflower_growth_1.jpg"
		);
	}

	@Test
	void 사용자_아이템_조회_성공() {
		when(plantGrowthItemRepository.findItemsByMemberId(1L))
			.thenReturn(List.of(new GetPlantGrowthItemResponse(
				plantGrowthItem.getItemName(),
				plantGrowthItem.getItemImgUrl(),
				plantGrowthItem.isApplicability()))
			);
		List<GetPlantGrowthItemResponse> items = plantItemService.getPlantGrowthItems(1L);

		assertThat(items).hasSize(1);
		assertThat(items.get(0).itemName()).isEqualTo(plantGrowthItem.getItemName());
		assertThat(items.get(0).itemImgUrl()).isEqualTo(plantGrowthItem.getItemImgUrl());
		assertThat(items.get(0).applicability()).isEqualTo(plantGrowthItem.isApplicability());
	}

	@Test
	void 사용자_아이템_장착여부_설정_성공() {
		when(plantGrowthItemRepository.findItemByIdAndMemberId(1L, 1L))
			.thenReturn(Optional.of(plantGrowthItem));

		assertThat(plantGrowthItem.isApplicability()).isFalse();

		plantItemService.changeApplicability(1L, 1L);

		assertThat(plantGrowthItem.isApplicability()).isTrue();
	}

	@Test
	void 사용자_아이템_장착여부_설정_재시도() {
		when(plantGrowthItemRepository.findItemByIdAndMemberId(1L, 1L))
			.thenThrow(ObjectOptimisticLockingFailureException.class)
			.thenReturn(Optional.of(plantGrowthItem));

		plantItemService.changeApplicability(1L, 1L);

		assertThat(plantGrowthItem.isApplicability()).isTrue();
	}

	@Test
	void 사용자_아이템_위치_변경_성공() {
		when(plantGrowthItemRepository.findItemByIdAndMemberId(1L, 1L))
			.thenReturn(Optional.of(plantGrowthItem));

		// 장착 후 위치 변경
		plantGrowthItem.apply();

		ChangePositionRequest request = new ChangePositionRequest(10.0, 20.0);
		ChangePositionGrowthItemResponse response =
			plantItemService.changePositionGrowthItem(1L, 1L, request);

		assertThat(response.positionX()).isEqualTo(10.0);
		assertThat(response.positionY()).isEqualTo(20.0);
	}

	@Test
	void 아이템_미장착_위치변경_실패() {
		when(plantGrowthItemRepository.findItemByIdAndMemberId(1L, 1L))
			.thenReturn(Optional.of(plantGrowthItem));

		ChangePositionRequest request = new ChangePositionRequest(10.0, 20.0);

		assertThatThrownBy(() -> plantItemService.changePositionGrowthItem(1L, 1L, request))
			.isInstanceOf(GrowthException.class)
			.hasMessage(GrowthExceptionMessage.NOT_SETTING_APPLICABILITY.getMessage());
	}

}


