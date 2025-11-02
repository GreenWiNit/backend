package com.example.green.domain.dashboard.growth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.green.domain.dashboard.growth.dto.LoadGrowthResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "사용자 식물 성장 데이터 조회 API", description = "로그인한 사용자의 식물 성장 데이터를 조회합니다.")
public interface GrowthControllerDocs {

	@GrowthCreateDocs
	ApiTemplate<LoadGrowthResponse> getGrowth(@AuthenticationPrincipal PrincipalDetails principall);
}
