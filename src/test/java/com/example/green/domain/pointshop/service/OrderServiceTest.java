package com.example.green.domain.pointshop.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.client.PointSpendClient;
import com.example.green.domain.pointshop.client.dto.PointSpendRequest;
import com.example.green.domain.pointshop.entity.order.Order;
import com.example.green.domain.pointshop.entity.order.OrderItem;
import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.ItemSnapshot;
import com.example.green.domain.pointshop.repository.OrderRepository;
import com.example.green.domain.pointshop.service.command.SingleOrderCommand;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private PointSpendClient pointSpendClient;
	@Mock
	private PointProductService pointProductService;
	@Mock
	private DeliveryAddressService deliveryAddressService;
	@Mock
	private OrderRepository orderRepository;
	@InjectMocks
	private OrderService orderService;

	@Test
	void 상품주문_커맨드가_주어지면_상품_주문_요청과_사이드_이펙트가_성공한다() {
		// given
		SingleOrderCommand command = new SingleOrderCommand(1L, "01ARZ3NDEKTSV4RRFFQ69G5FAV", 1L, 1L, 2);

		ItemSnapshot itemSnapshot = new ItemSnapshot(1L, "ITEM-001", "테스트 상품", BigDecimal.valueOf(10000));
		DeliveryAddressSnapshot deliverySnapshot = createDeliverySnapshot();
		Order mockOrder = createMockOrder();

		given(pointProductService.getItemSnapshot(1L)).willReturn(itemSnapshot);
		given(deliveryAddressService.getSnapshot(1L)).willReturn(deliverySnapshot);
		given(orderRepository.save(any(Order.class))).willReturn(mockOrder);

		// when
		Long orderId = orderService.orderSingleItem(command);

		// then
		assertThat(orderId).isEqualTo(1L);
		InOrder inOrder = inOrder(deliveryAddressService, pointProductService, orderRepository, pointSpendClient);
		inOrder.verify(deliveryAddressService).validateAddressOwnership(1L, 1L);
		inOrder.verify(pointProductService).decreaseSingleItemStock(1L, 2);
		inOrder.verify(pointSpendClient).spendPoints(any(PointSpendRequest.class));
	}

	private DeliveryAddressSnapshot createDeliverySnapshot() {
		return DeliveryAddressSnapshot.of(
			1L, "홍길동", "010-1234-5678",
			"서울시 강남구", "상세주소", "12345"
		);
	}

	private Order createMockOrder() {
		Order mockOrder = mock(Order.class);
		OrderItem mockOrderItem = mock(OrderItem.class);
		ItemSnapshot mockItemSnapshot = mock(ItemSnapshot.class);

		given(mockOrder.getId()).willReturn(1L);
		given(mockOrder.getTotalPrice()).willReturn(BigDecimal.valueOf(20000));
		given(mockOrder.getOrderItems()).willReturn(List.of(mockOrderItem));
		given(mockOrderItem.getItemSnapshot()).willReturn(mockItemSnapshot);
		given(mockItemSnapshot.getItemName()).willReturn("테스트 상품");

		return mockOrder;
	}
}