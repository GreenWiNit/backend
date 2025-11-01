package com.example.green.domain.dashboard.growth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.dashboard.growth.dto.LoadGrowthResponse;
import com.example.green.domain.dashboard.growth.message.GrowthResponseMessage;
import com.example.green.domain.dashboard.growth.service.GrowthService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard/growth")
public class GrowthController {

	private final GrowthService growthService;

	@GetMapping
	public ApiTemplate<LoadGrowthResponse> getGrowth(
		@AuthenticationPrincipal PrincipalDetails principal
	) {
		Long currentMemberId = principal.getMemberId();

		LoadGrowthResponse growthResponse = growthService.loadGrowth(currentMemberId);

		return ApiTemplate.ok(GrowthResponseMessage.LOAD_GROWTH_SUCCESS, growthResponse);
	}
}
