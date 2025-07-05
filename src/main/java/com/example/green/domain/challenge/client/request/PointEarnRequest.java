package com.example.green.domain.challenge.client.request;

import java.math.BigDecimal;

public record PointEarnRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String reason
) {
}
