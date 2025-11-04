package com.example.green.domain.pointshop.item.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "아이템 상품 수정 요청")
public record UpdatePointItemRequest(

	@NotBlank(message = "아이템 상품 코드는 비어 있을 수 없습니다.")
	@Schema(description = "아이템 상품 코드", example = "ITM-AA-001")
	String code,

	@NotBlank(message = "아이템 상품명은 비어있을 수 없습니다.")
	@Size(min = 2, max = 15, message = "아이템 상품명은 2글자 ~ 15글자 사이입니다.")
	@Schema(description = "아이템 상품명", example = "맑은 뭉게 구름")
	String name,

	@NotBlank(message = "아이템 상품 설명은 비어있을 수 없습니다.")
	@Size(max = 100, message = "아이템 상품 설명은 최대 100글자입니다.")
	@Schema(description = "아이템 상품 설명", example = "하늘에서 포근한 구름이 내려와 식물을 감싸요. 몽글몽글 기분 좋은 하루!")
	String description,

	@NotBlank(message = "아이템 상품 썸네일 이미지는 비어있을 수 없습니다.")
	@Schema(description = "아이템 상품 썸네일", example = "https://example.com/image.png")
	String thumbnailUrl,

	@NotNull(message = "아이템 상품 가격은 필수입니다.")
	@Schema(description = "아이템 상품 가격", example = "10000")
	BigDecimal price
) {
}
