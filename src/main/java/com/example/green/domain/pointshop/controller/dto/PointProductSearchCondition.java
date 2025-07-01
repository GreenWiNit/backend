package com.example.green.domain.pointshop.controller.dto;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;
import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 목록 조회 요청")
public record PointProductSearchCondition(
	@Schema(description = "페이지 (nullable)", example = "1")
	Integer page,
	@Schema(description = "페이지 당 항목 수 (nullable)", example = "10", defaultValue = "10")
	Integer size,
	@Schema(description = "상품코드 및 상품명 (nullable)", example = "텀블러")
	@Size(min = 2)
	String keyword,
	@Schema(description = "상품 판매 상태 (nullable)", type = "string", allowableValues = {"exchangeable", "sold-out"})
	SellingStatus status
) implements PageSearchCondition {
}
