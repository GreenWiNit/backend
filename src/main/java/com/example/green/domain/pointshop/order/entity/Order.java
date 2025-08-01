package com.example.green.domain.pointshop.order.entity;

import static com.example.green.domain.pointshop.order.exception.OrderExceptionMessage.*;
import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;
import com.example.green.domain.pointshop.order.entity.vo.OrderDeliveryStatus;
import com.example.green.domain.pointshop.order.exception.OrderException;
import com.example.green.domain.pointshop.order.exception.OrderExceptionMessage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "orders",
	uniqueConstraints = @UniqueConstraint(name = "uk_order_number", columnNames = "orderNumber"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Order extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	private String orderNumber;

	@Embedded
	private MemberSnapshot memberSnapshot;

	@Embedded
	private DeliveryAddressSnapshot deliveryAddressSnapshot;

	@Column(nullable = false)
	private BigDecimal totalPrice;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private OrderDeliveryStatus status;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<OrderItem> orderItems = new ArrayList<>();

	public static Order create(
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot,
		List<OrderItem> orderItems
	) {
		validateOrder(deliveryAddressSnapshot, memberSnapshot, orderItems);
		Order order = createOrder(deliveryAddressSnapshot, memberSnapshot);
		orderItems.forEach(order::addOrderItem);
		order.calculateTotalPrice();
		return order;
	}

	private static Order createOrder(
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot
	) {
		return Order.builder()
			.deliveryAddressSnapshot(deliveryAddressSnapshot)
			.memberSnapshot(memberSnapshot)
			.status(OrderDeliveryStatus.PENDING_DELIVERY)
			.build();
	}

	private static void validateOrder(
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot,
		List<OrderItem> orderItems
	) {
		validateNullData(deliveryAddressSnapshot, REQUIRE_DELIVERY_ADDRESS);
		validateNullData(memberSnapshot, REQUIRE_MEMBER_SNAPSHOT);
		validateEmptyList(orderItems, REQUIRE_ORDER_ITEM);
	}

	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
		orderItem.setOrder(this);
	}

	public void calculateTotalPrice() {
		this.totalPrice = this.orderItems.stream()
			.map(OrderItem::calculateItemFinalPrice)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void startShipping() {
		if (status != OrderDeliveryStatus.PENDING_DELIVERY) {
			throw new OrderException(OrderExceptionMessage.NO_PENDING_STATUS);
		}
		this.status = OrderDeliveryStatus.SHIPPING;
	}

	public void completeDelivery() {
		if (status != OrderDeliveryStatus.SHIPPING) {
			throw new OrderException(OrderExceptionMessage.NO_SHIPPING_STATUS);
		}
		this.status = OrderDeliveryStatus.DELIVERED;
	}
}
