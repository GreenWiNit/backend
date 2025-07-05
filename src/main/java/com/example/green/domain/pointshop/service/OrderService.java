package com.example.green.domain.pointshop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.order.Order;
import com.example.green.domain.pointshop.entity.order.OrderItem;
import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.ItemSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.MemberSnapshot;
import com.example.green.domain.pointshop.entity.point.vo.PointAmount;
import com.example.green.domain.pointshop.entity.point.vo.PointSource;
import com.example.green.domain.pointshop.entity.point.vo.TargetType;
import com.example.green.domain.pointshop.repository.OrderRepository;
import com.example.green.domain.pointshop.service.command.SingleOrderCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final PointProductService pointProductService;
	private final DeliveryAddressService deliveryAddressService;
	private final PointTransactionService pointTransactionService;
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
		PointAmount amount = PointAmount.of(savedOrder.getTotalPrice());
		PointSource pointSource = PointSource.ofTarget(savedOrder.getId(), itemName + " 교환", TargetType.ORDER);
		pointTransactionService.spendPoints(command.memberId(), amount, pointSource);
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
}
