package com.example.green.domain.point.entity;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.entity.vo.TargetType;
import com.example.green.domain.point.entity.vo.TransactionType;

class PointTransactionTest {

	@Test
	void 포인트_지급시_지급상태이며_거래후_잔액이_갱신된다() {
		// given
		PointSource source = PointSource.ofEvent("event 적립 입니다.");
		PointAmount pointAmount = PointAmount.of(BigDecimal.valueOf(1000));
		PointAmount currentBalance = PointAmount.of(BigDecimal.ZERO);
		LocalDateTime transactionAt = LocalDateTime.now();

		// when
		PointTransaction result = PointTransaction.earn(1L, source, pointAmount, currentBalance, transactionAt);

		// then
		PointAmount expect = currentBalance.add(pointAmount);
		assertThat(result.getBalanceAfter()).isEqualTo(expect);
		assertThat(result.getType()).isEqualTo(TransactionType.EARN);
		assertThat(result.getTransactionAt()).isEqualTo(transactionAt);
	}

	@Test
	void 포인트_사용시_사용상태이며_거래후_잔액이_갱신된다() {
		// given
		PointSource pointSource = PointSource.ofTarget(1L, "챌린지 적립입니다.", TargetType.CHALLENGE);
		PointAmount pointAmount = PointAmount.of(BigDecimal.valueOf(1000));
		PointAmount currentBalance = PointAmount.of(BigDecimal.valueOf(5000));
		LocalDateTime transactionAt = LocalDateTime.now();

		// when
		PointTransaction result = PointTransaction.spend(1L, pointSource, pointAmount, currentBalance, transactionAt);

		// then
		PointAmount expected = currentBalance.subtract(pointAmount);
		assertThat(result.getBalanceAfter()).isEqualTo(expected);
		assertThat(result.getType()).isEqualTo(TransactionType.SPEND);
		assertThat(result.getTransactionAt()).isEqualTo(transactionAt);
	}
}