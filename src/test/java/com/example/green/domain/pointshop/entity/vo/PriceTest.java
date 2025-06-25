package com.example.green.domain.pointshop.entity.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class PriceTest {

	@Test
	void 가격_정보를_생성한다() {
		// given
		// when
		Price price = new Price(1000);
		// then
		assertThat(price.getPrice()).isEqualTo(1000);
	}

	@ParameterizedTest
	@ValueSource(ints = -1)
	@NullSource
	void 가격은_필수값으로_0원_이상이_아니면_생성할_수_없다(Integer invalidPrice) {
		// given
		// when & then
		assertThatThrownBy(() -> new Price(invalidPrice))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_PRICE);
	}
}