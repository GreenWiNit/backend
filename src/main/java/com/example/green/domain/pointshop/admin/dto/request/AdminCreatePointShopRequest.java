package com.example.green.domain.pointshop.admin.dto.request;

import java.math.BigDecimal;

import com.example.green.domain.pointshop.admin.service.PointShopType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "상품 생성 요청")
@Builder
public record AdminCreatePointShopRequest(
	PointShopType type,

	@NotBlank(message = "상품 코드는 비어 있을 수 없습니다.")
	@Schema(description = "상품 코드", example = "ITM-AA-001")
	String code,

	@NotBlank(message = "상품명은 비어있을 수 없습니다.")
	@Size(min = 2, max = 15, message = "상품명은 2글자 ~ 15글자 사이입니다.")
	@Schema(description = "상품명", example = "맑은 뭉게 구름")
	String name,

	@NotNull(message = "상품 설명은 필수입니다.")
	@Size(max = 100, message = "상품 설명은 최대 100글자입니다.")
	@Schema(description = "상품 설명", example = "하늘에서 포근한 구름이 내려와 식물을 감싸요. 몽글몽글 기분 좋은 하루!")
	String description,

	@NotBlank(message = "상품 썸네일 이미지는 비어있을 수 없습니다.")
	@Schema(description = "상품 썸네일", example = "https://example.com/image.png")
	String thumbnailUrl,

	@NotNull(message = "상품 가격은 필수입니다.")
	@Schema(description = "상품 가격", example = "10000")
	BigDecimal price,

	@Schema(description = "상품 재고", example = "10")
	Integer stock
) {

}
