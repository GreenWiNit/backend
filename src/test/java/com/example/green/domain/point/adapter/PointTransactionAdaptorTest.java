package com.example.green.domain.point.adapter;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.challenge.client.request.PointEarnRequest;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

@ExtendWith(MockitoExtension.class)
class PointTransactionAdaptorTest {

	@Mock
	private PointTransactionService pointTransactionService;

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
}