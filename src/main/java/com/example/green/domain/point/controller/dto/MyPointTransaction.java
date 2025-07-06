package com.example.green.domain.point.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.point.entity.vo.TransactionType;

public record MyPointTransaction(
	Long pointTransactionId,
	String detailReason,
	BigDecimal amount,
	TransactionType status,
	LocalDateTime transactionAt
) {
}
