package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.entity.PointProduct;

public record PointProductDetail(
	long pointProductId,
	String code,
	String pointProductName,
	String description,
	String thumbnailUrl,
	BigDecimal pointPrice,
	int stockQuantity,
	String sellingStatus,
	boolean display
) {
	public static PointProductDetail from(PointProduct pointProduct) {
		return new PointProductDetail(
			pointProduct.getId(),
			pointProduct.getCode().getCode(),
			pointProduct.getBasicInfo().getName(),
			pointProduct.getBasicInfo().getDescription(),
			pointProduct.getThumbnailUrl(),
			pointProduct.getPrice().getPrice(),
			pointProduct.getStock().getStock(),
			pointProduct.getSellingStatus().getValue(),
			pointProduct.isDisplay()
		);
	}
}
