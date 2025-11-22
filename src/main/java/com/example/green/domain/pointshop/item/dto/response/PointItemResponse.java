package com.example.green.domain.pointshop.item.dto.response;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;

public record PointItemResponse(
	long pointItemId,
	String pointItemName,
	String thumbnailUrl,
	BigDecimal pointPrice,
	SellingStatus sellingStatus
) {
}
