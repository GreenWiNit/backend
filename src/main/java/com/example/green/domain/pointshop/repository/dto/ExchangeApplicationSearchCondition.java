package com.example.green.domain.pointshop.repository.dto;

import com.example.green.domain.pointshop.entity.order.vo.OrderDeliveryStatus;
import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 교환 신청 검색 조건")
public record ExchangeApplicationSearchCondition(
	@Schema(description = "상품 처리 상태 (nullable)",
		type = "string",
		allowableValues = {"pending-delivery", "shipping", "delivered"})
	OrderDeliveryStatus status,
	@Schema(description = "검색어 (nullable)")
	String keyword,
	@Schema(description = "검색 페이지 (nullable)")
	Integer page,
	@Schema(description = "페이지 사이즈 (nullable)")
	Integer size
) implements PageSearchCondition {
}
