package com.example.green.domain.pointshop.entity.order;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.MemberSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.OrderDeliveryStatus;
import com.example.green.domain.pointshop.exception.OrderException;
import com.example.green.domain.pointshop.exception.OrderExceptionMessage;

class OrderTest {

	String orderNumber;
	DeliveryAddressSnapshot deliveryAddressSnapshot;
	MemberSnapshot memberSnapshot;
	List<OrderItem> orderItems;
	Order order;
	BigDecimal finalPrice = BigDecimal.valueOf(1000);

	@BeforeEach
	void setUp() {
		orderNumber = "2025062823001";
		deliveryAddressSnapshot = DeliveryAddressSnapshot.of(1L, "수령자", "010-0000-0000", "도로명", "상세", "우편");
		memberSnapshot = new MemberSnapshot(1L, "memberCode");
		OrderItem orderItem = mock(OrderItem.class);
		when(orderItem.calculateItemFinalPrice()).thenReturn(finalPrice);
		orderItems = List.of(orderItem);
		order = Order.create(orderNumber, deliveryAddressSnapshot, memberSnapshot, orderItems);
	}

	@Test
	void 주문시_총_가격이_계산되고_배송_준비_상태가_된다() {
		// then
		assertThat(order.getOrderNumber()).isEqualTo(orderNumber);
		assertThat(order.getOrderItems()).isEqualTo(orderItems);
		assertThat(order.getTotalPrice()).isEqualTo(finalPrice);
		assertThat(order.getStatus()).isEqualTo(OrderDeliveryStatus.PENDING_DELIVERY);
	}

	@Test
	void 배송_준비_상태라면_배송을_시작한다() {
		// when
		order.startShipping();

		// then
		assertThat(order.getStatus()).isEqualTo(OrderDeliveryStatus.SHIPPING);
	}

	@Test
	void 배송_준비_상태가_아니라면_배송을_시작할_수_없다() {
		// given
		order.startShipping();

		// when & then
		assertThatThrownBy(() -> order.startShipping())
			.isInstanceOf(OrderException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", OrderExceptionMessage.NO_PENDING_STATUS);
	}

	@Test
	void 배송중인_상태라면_배송이_완료된다() {
		// given
		order.startShipping();

		// when
		order.completeDelivery();

		// then
		assertThat(order.getStatus()).isEqualTo(OrderDeliveryStatus.DELIVERED);
	}

	@Test
	void 배송중인_상태가_아니라면_배송을_완료할_수_없다() {
		// when & then
		assertThatThrownBy(() -> order.completeDelivery())
			.isInstanceOf(OrderException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", OrderExceptionMessage.NO_SHIPPING_STATUS);
	}
}