package com.example.green.domain.pointshop.controller.docs;

import com.example.green.domain.pointshop.repository.dto.PointProductApplicantResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "주문(상품 교환) API", description = "주문 관련 API 모음 입니다.")
public interface OrderAdminControllerDocs {

	@Operation(summary = "교환 신청자 정보 조회 (관리자)", description = "포인트 상품 교환 신청자 정보를 조회합니다.")
	@ApiResponse(responseCode = "200")
	ApiTemplate<PageTemplate<PointProductApplicantResult>> getExchangeApplicant(
		@Parameter(in = ParameterIn.PATH, description = "포인트 상품 식별자", required = true, example = "1")
		Long pointProductId,
		@Parameter(in = ParameterIn.QUERY, description = "현재 페이지 (nullable)", example = "1")
		Integer page,
		@Parameter(in = ParameterIn.QUERY, description = "페이지 당 사이즈 (nullable", example = "10")
		Integer size
	);
}
