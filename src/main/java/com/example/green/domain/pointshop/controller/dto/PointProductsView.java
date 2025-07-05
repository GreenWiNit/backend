package com.example.green.domain.pointshop.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

public record PointProductsView(
	Long pointProductId,
	String pointProductName,
	String thumbnailUrl,
	BigDecimal price,
	SellingStatus sellingStatus
) {
}
