package com.example.green.domain.point.adapter;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.challenge.client.request.PointEarnRequest;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.repository.PointTransactionRepository;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

@ExtendWith(MockitoExtension.class)
class PointTransactionAdaptorTest {

	@Mock
	private PointTransactionService pointTransactionService;
	@Mock
	private PointTransactionRepository pointTransactionRepository;

	@InjectMocks
	private PointTransactionAdaptor pointTransactionAdaptor;

	@Test
	void 주문_컨텍스트_요청을_처리한다() {
		// given
		PointSpendRequest dto = new PointSpendRequest(1L, BigDecimal.ONE, 1L, "description");

		// when
		pointTransactionAdaptor.spendPoints(dto);

		// then
		verify(pointTransactionService).spendPoints(anyLong(), any(PointAmount.class), any(PointSource.class));
	}

	@Test
	void 챌린지_컨텍스트_요청을_처리한다() {
		// given
		PointEarnRequest dto = new PointEarnRequest(1L, BigDecimal.ONE, 1L, "description");

		// when
		pointTransactionAdaptor.earnPoints(dto);

		// then
		verify(pointTransactionService).earnPoints(anyLong(), any(PointAmount.class), any(PointSource.class));
	}

	@Test
	void 멤버_컨텍스트_요청을_처리한다() {
		// given
		// when
		pointTransactionAdaptor.earnPoints(1L, "description", BigDecimal.ONE);

		// then
		verify(pointTransactionService).earnPoints(anyLong(), any(PointAmount.class), any(PointSource.class));
	}

	@Test
	void 사용자_식별자가_주어지면_사용자의_현재_포인트가_조회된다() {
		// given
		PointAmount pointAmount = PointAmount.of(BigDecimal.valueOf(10000));
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.of(pointAmount));

		// when
		BigDecimal totalPoints = pointTransactionAdaptor.getTotalPoints(1L);

		// then
		assertThat(totalPoints).isEqualTo(pointAmount.getAmount());
	}

	@Test
	void 사용자의_포인트_내역이_없다면_0원이_조회된다() {
		// given
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.empty());

		// when
		BigDecimal totalPoints = pointTransactionAdaptor.getTotalPoints(1L);

		// then
		PointAmount amount = PointAmount.ofZero();
		assertThat(totalPoints).isEqualTo(amount.getAmount());
	}
}