package com.example.green.domain.point.controller.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 내역")
public record MemberPointSummary(
	@Schema(description = "현재 포인트", example = "1000")
	BigDecimal currentBalance,
	@Schema(description = "총 적립 포인트", example = "10000")
	BigDecimal totalEarned,
	@Schema(description = "총 사용 포인트", example = "9000")
	BigDecimal totalSpent
) {
}
