package com.example.green.domain.dashboard.growth.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.growth.controller.docs.GrowthControllerDocs;
import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.message.GrowthResponseMessage;
import com.example.green.domain.dashboard.growth.service.GrowthService;
import com.example.green.domain.dashboard.growth.service.PlantItemService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/growth")
public class GrowthController implements GrowthControllerDocs {

	private final GrowthService growthService;

	private final PlantItemService plantItemService;

	@AuthenticatedApi(reason = "자신의 성장 정보를 조회할 수 있습니다")
	@GetMapping
	public ApiTemplate<LoadGrowthResponse> getGrowth(
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		Long currentMemberId = principal.getMemberId();

		LoadGrowthResponse growthResponse = growthService.loadGrowth(currentMemberId);

		return ApiTemplate.ok(GrowthResponseMessage.LOAD_GROWTH_SUCCESS, growthResponse);
	}

	@AuthenticatedApi(reason = "포인트 상점에서 교환한 아이템을 조회 할 수 있습니다")
	@GetMapping("/items")
	public ApiTemplate<List<GetPlantGrowthItemResponse>> getAllGrowth(
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		Long currentMemberId = principal.getMemberId();
		List<GetPlantGrowthItemResponse> items = plantItemService.getPlantGrowthItems(currentMemberId);
		return ApiTemplate.ok(GrowthResponseMessage.LOAD_ITEMS_SUCCESS, items);

	}

	@AuthenticatedApi(reason = "아이템 장착 여부를 설정할 수 있습니다")
	@PatchMapping("/{itemId}")
	public ApiTemplate updateApplicability(
		@AuthenticationPrincipal PrincipalDetails principal,
		@PathVariable Long itemId
	) {
		Long currentMemberId = principal.getMemberId();
		plantItemService.changeApplicability(currentMemberId, itemId);
		return ApiTemplate.ok(GrowthResponseMessage.CHANGE_APPLICABILITY);
	}
}
