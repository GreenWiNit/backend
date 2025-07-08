package com.example.green.domain.pointshop.repository.dto;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 목록 엑셀 다운로드 요청")
public record PointProductExcelCondition(
	@Schema(description = "상품코드 및 상품명 (nullable)", example = "텀블러")
	@Size(min = 2)
	String keyword,
	@Schema(description = "상품 판매 상태 (nullable)", type = "string", allowableValues = {"exchangeable", "sold-out"})
	SellingStatus status
) {
}
