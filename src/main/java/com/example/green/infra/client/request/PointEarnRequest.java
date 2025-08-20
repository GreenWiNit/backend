package com.example.green.infra.client.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PointEarnRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description,
	String type,
	LocalDateTime transactionAt
) {
}
