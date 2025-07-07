package com.example.green.domain.point.repository.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.point.entity.vo.TransactionType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 내역 조회")
public record MyPointTransaction(
	@Schema(description = "포인트 내역 식별자", example = "1L")
	Long pointTransactionId,
	@Schema(description = "포인트 적립 및 차감 내용", example = "챌린지 적립")
	String detailReason,
	@Schema(description = "적립 및 차감 포인트", example = "1000")
	BigDecimal amount,
	@Schema(description = "포인트 트랜잭션 상태")
	TransactionType status,
	@Schema(description = "포인트 트랜잭션 처리 시간")
	LocalDateTime transactionAt
) {
}
