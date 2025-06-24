package com.example.green.domain.pointshop.entity.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class StockTest {

	@Test
	void 재고를_생성한다() {
		// given
		// when
		Stock stock = new Stock(100);

		// then
		assertThat(stock.stock()).isEqualTo(100);
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	@NullSource
	void 상품_생성시_상품_재고는_필수값으로_1개_이상이_아니라면_생성할_수_없다(Integer invalidStock) {
		// given
		// when & then
		assertThatThrownBy(() -> new Stock(invalidStock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_STOCK);
	}

	@Test
	void 상품_재고를_감소하면_남은_재고를_반환한다() {
		// given
		int amount = 50;
		Stock stock = new Stock(100);

		// when
		Integer decreased = stock.decrease(amount);

		// then
		assertThat(decreased).isEqualTo(100 - amount);
	}

	@Test
	void 상품_재고보다_많은_수량으로_감소하면_예외가_발생한다() {
		// given
		int amount = 101;
		Stock stock = new Stock(100);

		// when & then
		assertThatThrownBy(() -> stock.decrease(amount))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", OUT_OF_PRODUCT_STOCK);
	}
}