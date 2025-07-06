package com.example.green.domain.point.controller.dto;

import java.math.BigDecimal;

public record MemberPointSummary(
	BigDecimal currentBalance,
	BigDecimal totalEarned,
	BigDecimal totalSpent
) {
}
