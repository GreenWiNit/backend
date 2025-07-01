package com.example.green.domain.pointshop.controller.dto;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 목록 조회 요청")
public record PointProductSearchCondition(
	@Schema(description = "페이지", example = "1, Can null")
	Integer page,
	@Schema(description = "페이지 당 항목 수", example = "10, Can null")
	Integer size,
	@Schema(description = "상품코드 및 상품명", example = "텀블러, Can null")
	String keyword,
	@Schema(description = "상품 판매 상태", example = "EXCHANGEABLE, Can null")
	SellingStatus status
) {
}
