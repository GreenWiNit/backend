package com.example.green.domain.pointshop.item.dto.response;

import java.math.BigDecimal;

public record OrderPointItemResponse(
	Long memberId,
	String itemName,
	String itemImgUrl,
	BigDecimal remainPoint,
	Integer amount //수량
) {
}
