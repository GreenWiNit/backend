package com.example.green.domain.pointshop.order.client.dto;

import java.math.BigDecimal;

public record PointSpendRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description
) {
}
