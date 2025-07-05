package com.example.green.domain.pointshop.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.entity.pointproduct.vo.SellingStatus;

public record PointProductView(
	long pointProductId,
	String pointProductName,
	String thumbnailUrl,
	BigDecimal pointPrice,
	SellingStatus sellingStatus
) {
}
