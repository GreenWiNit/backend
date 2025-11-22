package com.example.green.domain.pointshop.item.dto.request;

import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;
import com.example.green.global.api.page.PageSearchCondition;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "아이템 목록 조회 요청")
public record PointItemSearchRequest(
	@Schema(description = "페이지 (nullable)", example = "1")
	Integer page,
	@Schema(description = "페이지 당 항목 수 (nullable)", example = "10", defaultValue = "10")
	Integer size,
	@Schema(description = "아이템 코드 및 아이템 명", example = "맑은 뭉게 구름 ")
	@Size(min = 2)
	String keyword,
	@Schema(description = "아이템 판매 상태", type = "string", allowableValues = {"exchangeable", "sold-out"})
	SellingStatus status
) implements PageSearchCondition {
}
