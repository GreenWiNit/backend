package com.example.green.domain.pointshop.item.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "아이템 상품 주문 요청")
public record OrderPointItemRequest(

	@NotNull(message = "아이템 상품 주문수는 비어 있을 수 없습니다.")
	@Min(value = 1, message = "아이템 상품 주문수는 1개 이상이어야 합니다.")
	@Schema(description = "아이템 상품 수", example = "3")
	Integer amount
) {
}
