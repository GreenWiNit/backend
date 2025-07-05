package com.example.green.domain.point.entity.vo;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.example.green.domain.pointshop.exception.point.PointException;
import com.example.green.domain.pointshop.exception.point.PointExceptionMessage;

class PointAmountTest {

	@Test
	void 포인트_금액은_음수일_수_없다() {
		// given

		// when & then
		assertThatThrownBy(() -> PointAmount.of(BigDecimal.valueOf(-1)))
			.isInstanceOf(PointException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PointExceptionMessage.INVALID_POINT_AMOUNT);
	}

	@Test
	void 포인트_금액은_소수점_이하_두자리까지_표현한다() {
		// given
		BigDecimal inputValue = new BigDecimal("100.567");

		// when
		PointAmount amount = PointAmount.of(inputValue);

		// then
		assertThat(amount.getAmount()).isEqualTo(new BigDecimal("100.56"));
		assertThat(amount.getAmount().scale()).isEqualTo(2);
	}

	@Test
	void 포인트_금액_간_더할_수_있다() {
		// given
		PointAmount amount1 = PointAmount.of(BigDecimal.ZERO);
		PointAmount amount2 = PointAmount.of(BigDecimal.valueOf(1000));

		// when
		PointAmount result = amount1.add(amount2);

		// then
		assertThat(result).isEqualTo(PointAmount.of(BigDecimal.valueOf(1000)));
	}

	@Test
	void 포인트_금액_간_뺄_수_있다() {
		// given
		PointAmount amount1 = PointAmount.of(BigDecimal.valueOf(400));
		PointAmount amount2 = PointAmount.of(BigDecimal.valueOf(1000));

		// when
		PointAmount result = amount2.subtract(amount1);

		// then
		assertThat(result).isEqualTo(PointAmount.of(BigDecimal.valueOf(600)));
	}

	@Test
	void 포인트_금액_간_뺄셈시_음수라면_예외가_발생한다() {
		// given
		PointAmount amount1 = PointAmount.of(BigDecimal.valueOf(400));
		PointAmount amount2 = PointAmount.of(BigDecimal.valueOf(1000));

		// when & then
		assertThatThrownBy(() -> amount1.subtract(amount2))
			.isInstanceOf(PointException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PointExceptionMessage.INVALID_POINT_AMOUNT);
	}
}