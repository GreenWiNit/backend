package com.example.green.domain.pointshop.entity.pointproduct.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class PriceTest {

	@ParameterizedTest
	@ValueSource(ints = -1)
	void 가격은_필수값으로_0원_이상이_아니면_생성할_수_없다(Number invalidPrice) {
		// given
		BigDecimal value = BigDecimal.valueOf(invalidPrice.doubleValue());
		// when & then
		assertThatThrownBy(() -> new Price(value))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_PRICE);
	}
}