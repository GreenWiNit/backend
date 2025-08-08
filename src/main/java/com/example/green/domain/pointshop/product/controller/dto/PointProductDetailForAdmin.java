package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.product.entity.PointProduct;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Point 상품 상세 조회")
public record PointProductDetailForAdmin(
	@Schema(description = "상품 아이디", example = "1")
	long pointProductId,
	@Schema(description = "상품 코드", example = "PRD-AB-001")
	String code,
	@Schema(description = "상품명", example = "텀블러")
	String pointProductName,
	@Schema(description = "상품 설명", example = "텀블러 소개")
	String description,
	@Schema(description = "이미지 url", example = "url")
	String thumbnailUrl,
	@Schema(description = "상품 가격", example = "1000")
	BigDecimal pointPrice,
	@Schema(description = "상품 재고 수량", example = "10")
	int stockQuantity,
	@Schema(description = "판매 가능 여부", example = "교환가능", allowableValues = {"교환가능", "품절"})
	String sellingStatus,
	@Schema(description = "상품 전시 상태 (사용자는 null)", example = "true", allowableValues = {"null", "true", "false"})
	Boolean display
) {
	public static PointProductDetailForAdmin from(PointProduct pointProduct) {
		return new PointProductDetailForAdmin(
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
