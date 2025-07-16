package com.example.green.domain.pointshop.product.controller.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "상품 수정 요청")
public record PointProductUpdateDto(
	@NotBlank(message = "상품 코드는 비어 있을 수 없습니다.")
	@Schema(description = "상품 코드", example = "PRD-AA-001")
	String code,
	@NotBlank(message = "상품명은 비어 있을 수 없습니다.")
	@Size(min = 2, max = 15, message = "상품명은 2글자 ~ 15글자 사이입니다.")
	@Schema(description = "상품명", example = "어피치 텀블러")
	String name,
	@NotNull(message = "상품 설명은 필수입니다.")
	@Size(max = 100, message = "상품 설명은 최대 100글자입니다.")
	@Schema(description = "상품 설명", example = "한정판 텀블러입니다.")
	String description,
	@NotBlank(message = "상품 썸네일 이미지는 비어있을 수 없습니다.")
	@Schema(description = "상품 썸네일", example = "https://example.com/image.png")
	String thumbnailUrl,
	@NotNull(message = "상품 가격은 필수입니다.")
	@Schema(description = "상품 가격", example = "10000")
	BigDecimal price,
	@NotNull(message = "상품 재고는 필수입니다.")
	@Schema(description = "상품 재고", example = "10")
	Integer stock
) {
}
