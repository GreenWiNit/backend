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

class OrderTest {

	String orderNumber;
	DeliveryAddressSnapshot deliveryAddressSnapshot;
	MemberSnapshot memberSnapshot;
	List<OrderItem> orderItems;
	Order order;

	@BeforeEach
	void setUp() {
		orderNumber = "2025062823001";
		deliveryAddressSnapshot = DeliveryAddressSnapshot.of(1L, "수령자", "010-0000-0000", "도로명", "상세", "우편");
		memberSnapshot = new MemberSnapshot(1L, "memberCode");
	}

	@Test
	void 주문시_총_가격이_계산되고_배송_준비_상태가_된다() {
		// given
		BigDecimal finalPrice = BigDecimal.valueOf(1000);
		OrderItem orderItem = mock(OrderItem.class);
		when(orderItem.calculateItemFinalPrice()).thenReturn(finalPrice);
		orderItems = List.of(orderItem);

		// when
		order = Order.create(orderNumber, deliveryAddressSnapshot, memberSnapshot, orderItems);

		// then
		assertThat(order.getOrderNumber()).isEqualTo(orderNumber);
		assertThat(order.getOrderItems()).isEqualTo(orderItems);
		assertThat(order.getTotalPrice()).isEqualTo(finalPrice);
		assertThat(order.getStatus()).isEqualTo(OrderDeliveryStatus.PENDING_DELIVERY);
	}
}