package com.example.green.domain.point.repository.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.point.entity.vo.TargetType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 내역 조회")
public record PointTransactionDto(
	@Schema(description = "포인트 내역 식별자", example = "1L")
	Long pointTransactionId,
	@Schema(description = "포인트 적립 종류")
	TargetType type,
	@Schema(description = "포인트 적립 및 차감 내용", example = "챌린지 적립")
	String description,
	@Schema(description = "적립 포인트", example = "1000")
	BigDecimal earnedAmount,
	@Schema(description = "차감 포인트", example = "0")
	BigDecimal spentAmount,
	@Schema(description = "거래 후 포인트", example = "1000")
	BigDecimal balanceAfter,
	@Schema(description = "포인트 트랜잭션 처리 시간")
	LocalDateTime transactionAt
) {

	public LocalDateTime getTransactionAt() {
		if (transactionAt == null) {
			return LocalDateTime.of(2025, 1, 1, 0, 0);
		}
		return transactionAt;
	}
}
