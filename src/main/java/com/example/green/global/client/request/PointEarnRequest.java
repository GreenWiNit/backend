package com.example.green.global.client.request;

import java.math.BigDecimal;

public record PointEarnRequest(
	Long memberId,
	BigDecimal amount,
	Long targetId,
	String description
) {
}
