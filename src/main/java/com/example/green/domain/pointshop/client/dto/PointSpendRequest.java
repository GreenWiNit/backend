package com.example.green.domain.pointshop.client.dto;

import java.math.BigDecimal;

public record PointSpendRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description
) {
}
