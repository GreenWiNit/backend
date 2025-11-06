package com.example.green.domain.pointshop.item.dto.response;

import java.math.BigDecimal;

public record PointItemResponse(
	long pointItemId,
	String pointItemName,
	String thumbnailUrl,
	BigDecimal pointPrice
) {
}
