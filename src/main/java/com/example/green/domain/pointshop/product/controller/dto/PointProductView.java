package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.entity.vo.SellingStatus;

public record PointProductView(
	long pointProductId,
	String pointProductName,
	String thumbnailUrl,
	BigDecimal pointPrice,
	SellingStatus sellingStatus
) {
}
