package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.entity.PointProduct;

public record PointProductDetail(
	long pointProductId,
	String pointProductName,
	String description,
	String thumbnailUrl,
	BigDecimal pointPrice,
	int stockQuantity
) {
	public static PointProductDetail from(PointProduct pointProduct) {
		return new PointProductDetail(
			pointProduct.getId(),
			pointProduct.getBasicInfo().getName(),
			pointProduct.getBasicInfo().getDescription(),
			pointProduct.getThumbnailUrl(),
			pointProduct.getPrice().getPrice(),
			pointProduct.getStock().getStock()
		);
	}
}
