package com.example.green.infra.client.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointSpendRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description,
	LocalDateTime transactionAt
) {
}
