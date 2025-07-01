package com.example.green.domain.pointshop.controller.dto;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 목록 조회 요청")
public record PointProductSearchCondition(
	@Schema(description = "페이지 (nullable)", example = "1")
	Integer page,
	@Schema(description = "페이지 당 항목 수 (nullable)", example = "10", defaultValue = "10")
	Integer size,
	@Schema(description = "상품코드 및 상품명 (nullable)", example = "텀블러")
	String keyword,
	@Schema(type = "string",
		description = "상품 판매 상태 (nullable)",
		allowableValues = {"exchangeable", "sold_out"})
	SellingStatus status
) {
}
