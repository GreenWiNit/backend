package com.example.green.domain.pointshop.entity.order;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.domain.pointshop.entity.order.vo.ItemSnapshot;
import com.example.green.domain.pointshop.exception.OrderExceptionMessage;
import com.example.green.global.error.exception.BusinessException;

class OrderItemTest {

	ItemSnapshot itemSnapshot = new ItemSnapshot(1L, "ItemName", "ItemCOde", BigDecimal.valueOf(500));

	@Test
	void 아이템을_수량에_맞게_주문한다() {
		// given
		int quantity = 5;

		// when
		OrderItem orderItem = OrderItem.create(itemSnapshot, quantity);

		// then
		assertThat(orderItem.getQuantity()).isEqualTo(quantity);
	}

	@ParameterizedTest
	@ValueSource(ints = {0, 6})
	void 아이템_주문시_수량_제한이_있다(int invalidQuantity) {
		// given
		// when & then
		assertThatThrownBy(() -> OrderItem.create(itemSnapshot, invalidQuantity))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", OrderExceptionMessage.INVALID_QUANTITY_COUNT);
	}

	@Test
	void 아이템_수량에_따른_최종_주문_가격을_알_수_있다() {
		// given
		int quantity = 5;
		OrderItem orderItem = OrderItem.create(itemSnapshot, quantity);

		// when
		BigDecimal finalPrice = orderItem.calculateItemFinalPrice();

		// then
		BigDecimal unitPrice = itemSnapshot.getUnitPrice();
		BigDecimal expected = unitPrice.multiply(BigDecimal.valueOf(quantity));
		assertThat(finalPrice).isEqualTo(expected);
	}
}