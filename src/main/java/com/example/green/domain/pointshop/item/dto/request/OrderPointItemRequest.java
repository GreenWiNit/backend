package com.example.green.domain.pointshop.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "아이템 상품 주문 요청")
public record OrderPointItemRequest(

	@NotBlank(message = "아이템 상품 주문수는 비어 있을 수 없습니다.")
	@Schema(description = "아이템 상품 수", example = "3")
	Integer amount
) {
}
