package com.example.green.domain.pointshop.item.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 아이템 상품 구매자 정보")
public record BuyerInformation(
	@Schema(description = "사용자 키", example = "member_key")
	String memberKey,
	String memberEmail
) {
}
