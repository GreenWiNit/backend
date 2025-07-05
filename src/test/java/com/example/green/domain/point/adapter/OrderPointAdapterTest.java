package com.example.green.domain.point.adapter;

import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.service.PointTransactionService;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;

@ExtendWith(MockitoExtension.class)
class OrderPointAdapterTest {

	@Mock
	private PointTransactionService pointTransactionService;

	@InjectMocks
	private OrderPointAdapter orderPointAdapter;

	@Test
	void 주문_컨텍스트_요청을_처리한다() {
		// given
		PointSpendRequest dto = new PointSpendRequest(1L, BigDecimal.ONE, 1L, "reason");

		// when
		orderPointAdapter.spendPoints(dto);

		// then
		verify(pointTransactionService).spendPoints(anyLong(), any(PointAmount.class), any(PointSource.class));
	}
}