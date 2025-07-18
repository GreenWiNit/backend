package com.example.green.domain.pointshop.product.entity.vo;

import static com.example.green.domain.pointshop.product.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class StockTest {

	@Test
	void 재고를_생성한다() {
		// given
		// when
		Stock stock = new Stock(100);

		// then
		assertThat(stock.getStock()).isEqualTo(100);
	}

	@Test
	void 상품_재고는_NULL값_일_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> new Stock(null)).isInstanceOf(BusinessException.class);
	}

	@ParameterizedTest
	@ValueSource(ints = {-1})
	void 상품_생성시_상품_재고는_필수값으로_0개_이상이_아니라면_생성할_수_없다(Integer invalidStock) {
		// given
		// when & then
		assertThatThrownBy(() -> new Stock(invalidStock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_STOCK);
	}

	@Test
	void 상품_재고를_감소하면_남은_재고를_반환한다() {
		// given
		Stock stock = new Stock(100);
		int amount = 50;

		// when
		Stock result = stock.decrease(amount);

		// then
		assertThat(result.getStock()).isEqualTo(50);
	}

	@Test
	void 상품_재고보다_많은_수량으로_감소하면_예외가_발생한다() {
		// given
		Stock stock = new Stock(100);
		int amount = 101;

		// when & then
		assertThatThrownBy(() -> stock.decrease(amount))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", OUT_OF_PRODUCT_STOCK);
	}

	@Test
	void 상품_재고가_0개라면_매진이다() {
		// given
		Stock stock = new Stock(0);

		// when
		boolean result = stock.isSoldOut();

		// then
		assertThat(result).isTrue();
	}

	@Test
	void 상품_재고가_0개가_아니라면_매진이_아니다() {
		// given
		Stock stock = new Stock(1);

		// when
		boolean result = stock.isSoldOut();

		// then
		assertThat(result).isFalse();
	}
}