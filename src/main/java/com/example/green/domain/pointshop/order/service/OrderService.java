package com.example.green.domain.pointshop.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.delivery.service.DeliveryAddressService;
import com.example.green.domain.pointshop.order.entity.Order;
import com.example.green.domain.pointshop.order.entity.OrderItem;
import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.domain.pointshop.order.exception.OrderException;
import com.example.green.domain.pointshop.order.exception.OrderExceptionMessage;
import com.example.green.domain.pointshop.order.repository.OrderRepository;
import com.example.green.domain.pointshop.order.service.command.SingleOrderCommand;
import com.example.green.domain.pointshop.product.service.PointProductService;
import com.example.green.global.client.PointClient;
import com.example.green.global.client.request.PointSpendRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final PointProductService pointProductService;
	private final DeliveryAddressService deliveryAddressService;
	private final PointClient pointClient;
	private final OrderRepository orderRepository;

	// todo: 통합 테스트
	@Transactional
	public Long orderSingleItem(SingleOrderCommand command) {
		MemberSnapshot memberSnapshot = command.memberSnapshot();
		deliveryAddressService.validateAddressOwnership(command.deliveryAddressId(), memberSnapshot.getMemberId());

		ItemSnapshot itemSnapshot = pointProductService.getItemSnapshot(command.orderItemId());
		DeliveryAddressSnapshot deliveryAddress = deliveryAddressService.getSnapshot(command.deliveryAddressId());

		Order savedOrder = processOrder(command.quantity(), itemSnapshot, deliveryAddress, memberSnapshot);
		processSideEffect(command, memberSnapshot.getMemberId(), savedOrder);
		return savedOrder.getId();
	}

	private void processSideEffect(SingleOrderCommand command, Long memberId, Order savedOrder) {
		pointProductService.decreaseSingleItemStock(command.orderItemId(), command.quantity());
		String itemName = savedOrder.getOrderItems().getFirst().getItemSnapshot().getItemName();
		pointClient.spendPoints(
			new PointSpendRequest(memberId, savedOrder.getTotalPrice(), savedOrder.getId(), itemName + "교환")
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
