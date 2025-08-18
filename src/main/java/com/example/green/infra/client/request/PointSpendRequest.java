package com.example.green.infra.client.request;

import java.math.BigDecimal;

public record PointSpendRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description
) {
}
