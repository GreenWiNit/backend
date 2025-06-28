package com.example.green.domain.pointshop.entity.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.pointshop.entity.order.vo.DeliveryAddressSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.MemberSnapshot;
import com.example.green.domain.pointshop.entity.order.vo.OrderDeliveryStatus;
import com.example.green.domain.pointshop.exception.OrderException;
import com.example.green.domain.pointshop.exception.OrderExceptionMessage;
import com.example.green.global.utils.EntityValidator;

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
	@Column(nullable = false, updatable = false)
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
		String orderNumber,
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot,
		List<OrderItem> orderItems
	) {
		validateOrder(orderNumber, deliveryAddressSnapshot, memberSnapshot, orderItems);
		Order order = createOrder(orderNumber, deliveryAddressSnapshot, memberSnapshot);
		orderItems.forEach(order::addOrderItem);
		order.calculateTotalPrice();
		return order;
	}

	private static Order createOrder(
		String orderNumber,
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot
	) {
		return Order.builder()
			.orderNumber(orderNumber)
			.deliveryAddressSnapshot(deliveryAddressSnapshot)
			.memberSnapshot(memberSnapshot)
			.status(OrderDeliveryStatus.PENDING_DELIVERY)
			.build();
	}

	private static void validateOrder(
		String orderNumber,
		DeliveryAddressSnapshot deliveryAddressSnapshot,
		MemberSnapshot memberSnapshot,
		List<OrderItem> orderItems
	) {
		EntityValidator.validateEmptyString(orderNumber, "주문 번호는 필수 값 입니다.");
		EntityValidator.validateNullData(deliveryAddressSnapshot, "상품 주문 시 배송지 정보는 필수 값입니다.");
		EntityValidator.validateNullData(memberSnapshot, "상품 주문 시 주문자 정보는 필수 값 입니다.");
		EntityValidator.validateEmptyList(orderItems, "상품 주문시 상품 정보는 1개 이상 필요합니다.");
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
