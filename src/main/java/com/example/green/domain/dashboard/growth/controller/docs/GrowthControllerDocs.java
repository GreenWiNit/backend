package com.example.green.domain.dashboard.growth.controller.docs;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.dashboard.growth.dto.request.ChangePositionRequest;
import com.example.green.domain.dashboard.growth.dto.response.ChangePositionGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.GetPlantGrowthItemResponse;
import com.example.green.domain.dashboard.growth.dto.response.LoadGrowthResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "사용자 식물 성장 데이터 조회 API", description = "로그인한 사용자의 식물 성장 데이터를 조회합니다.")
public interface GrowthControllerDocs {

	@GrowthCreateDocs
	ApiTemplate<LoadGrowthResponse> getGrowth(@AuthenticationPrincipal PrincipalDetails principal);

	@GrowthItemDocs
	ApiTemplate<List<GetPlantGrowthItemResponse>> getAllGrowth(@AuthenticationPrincipal PrincipalDetails principal);

	@GrowthApplicabilityDocs
	ApiTemplate updateApplicability(@AuthenticationPrincipal PrincipalDetails principal, Long itemId);

	@GrowthPositionDocs
	ApiTemplate<ChangePositionGrowthItemResponse> changePositionGrowth(
		@AuthenticationPrincipal PrincipalDetails principal,
		Long itemId,
		ChangePositionRequest changePositionRequest
	);
}
