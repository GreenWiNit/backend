package com.example.green.domain.pointshop.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.order.client.PointSpendClient;
import com.example.green.domain.pointshop.order.client.dto.PointSpendRequest;
import com.example.green.domain.pointshop.order.entity.Order;
import com.example.green.domain.pointshop.order.entity.OrderItem;
import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.domain.pointshop.order.exception.OrderException;
import com.example.green.domain.pointshop.order.exception.OrderExceptionMessage;
import com.example.green.domain.pointshop.order.repository.OrderRepository;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;
import com.example.green.domain.pointshop.delivery.service.DeliveryAddressService;
import com.example.green.domain.pointshop.product.service.PointProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final PointProductService pointProductService;
	private final DeliveryAddressService deliveryAddressService;
	private final PointSpendClient pointSpendClient;
	private final OrderRepository orderRepository;

	// todo: 통합 테스트
	public Long orderSingleItem(SingleOrderCommand command) {
		deliveryAddressService.validateAddressOwnership(command.deliveryAddressId(), command.memberId());

		ItemSnapshot itemSnapshot = pointProductService.getItemSnapshot(command.orderItemId());
		MemberSnapshot memberSnapshot = new MemberSnapshot(command.memberId(), command.memberCode());
		DeliveryAddressSnapshot deliveryAddress = deliveryAddressService.getSnapshot(command.deliveryAddressId());

		Order savedOrder = processOrder(command.quantity(), itemSnapshot, deliveryAddress, memberSnapshot);
		processSideEffect(command, savedOrder);
		return savedOrder.getId();
	}

	private void processSideEffect(SingleOrderCommand command, Order savedOrder) {
		pointProductService.decreaseSingleItemStock(command.orderItemId(), command.quantity());
		String itemName = savedOrder.getOrderItems().getFirst().getItemSnapshot().getItemName();
		pointSpendClient.spendPoints(
			new PointSpendRequest(command.memberId(), savedOrder.getTotalPrice(), savedOrder.getId(), itemName + "교환")
		);
	}

	private Order processOrder(
		int quantity,
		ItemSnapshot itemSnapshot,
		DeliveryAddressSnapshot deliveryAddress,
		MemberSnapshot memberSnapshot
	) {
		OrderItem orderItem = OrderItem.create(itemSnapshot, quantity);
		Order order = Order.create(deliveryAddress, memberSnapshot, List.of(orderItem));
		return orderRepository.save(order);
	}

	public void shipOrder(Long orderId) {
		orderRepository.findById(orderId)
			.ifPresentOrElse(Order::startShipping, () -> {
				throw new OrderException(OrderExceptionMessage.NOT_FOUND_ORDER);
			});
	}

	public void completeDelivery(Long orderId) {
		orderRepository.findById(orderId)
			.ifPresentOrElse(Order::completeDelivery, () -> {
				throw new OrderException(OrderExceptionMessage.NOT_FOUND_ORDER);
			});
	}
}
