package com.example.green.domain.point.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.point.entity.PointTransaction;
import com.example.green.domain.point.entity.vo.PointAmount;
import com.example.green.domain.point.entity.vo.PointSource;
import com.example.green.domain.point.exception.PointException;
import com.example.green.domain.point.exception.PointExceptionMessage;
import com.example.green.domain.point.repository.PointTransactionRepository;

@ExtendWith(MockitoExtension.class)
class PointTransactionServiceTest {

	@Mock
	private PointTransactionRepository pointTransactionRepository;

	@InjectMocks
	private PointTransactionService pointTransactionService;

	@Test
	void 사용자Id와_사용_금액_포인트_출처_정보로_포인트_차감_내역을_저장한다() {
		// given
		PointAmount mock = mock(PointAmount.class);
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.of(mock));
		when(mock.canSpend(any(PointAmount.class))).thenReturn(true);

		// when
		pointTransactionService.spendPoints(
			1L, PointAmount.of(BigDecimal.valueOf(1000)), mock(PointSource.class), mock(LocalDateTime.class));

		// then
		verify(pointTransactionRepository).save(any(PointTransaction.class));
	}

	@Test
	void 포인트_차감_시_사용_가능한_포인트가_충분하지_않으면_예외가_발생한다() {
		// given
		PointAmount mock = mock(PointAmount.class);
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.of(mock));
		when(mock.canSpend(any(PointAmount.class))).thenReturn(false);

		assertThatThrownBy(() ->
			pointTransactionService.spendPoints(
				1L, PointAmount.of(BigDecimal.ZERO), mock(PointSource.class), mock(LocalDateTime.class)
			))
			.isInstanceOf(PointException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PointExceptionMessage.NOT_ENOUGH_POINT);
	}

	@Test
	void 포인트_적립_시_기존_포인트에_추가_지급한다() {
		// given
		PointAmount mock = mock(PointAmount.class);
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.of(mock));

		// when
		pointTransactionService.earnPoints(
			1L, PointAmount.of(BigDecimal.valueOf(1000)), mock(PointSource.class), mock(LocalDateTime.class)
		);

		// then
		verify(pointTransactionRepository).save(any(PointTransaction.class));
	}

	@Test
	void 포인트_적립_시_기존_포인트가_없어도_지급이_된다() {
		// given
		when(pointTransactionRepository.findLatestBalance(anyLong())).thenReturn(Optional.empty());

		// when
		pointTransactionService.earnPoints(
			1L, PointAmount.of(BigDecimal.valueOf(1000)), mock(PointSource.class), mock(LocalDateTime.class)
		);

		// then
		verify(pointTransactionRepository).save(any(PointTransaction.class));
	}
}