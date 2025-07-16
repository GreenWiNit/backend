package com.example.green.domain.pointshop.order.entity;

import static com.example.green.global.utils.EntityValidator.*;

import java.math.BigDecimal;

import com.example.green.domain.common.TimeBaseEntity;
import com.example.green.domain.pointshop.order.entity.vo.ItemSnapshot;
import com.example.green.domain.pointshop.order.exception.OrderException;
import com.example.green.domain.pointshop.order.exception.OrderExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends TimeBaseEntity {

	private static final int MIN_QUANTITY = 1;
	private static final int MAX_QUANTITY = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_item_id")
	private Long id;

	@Embedded
	private ItemSnapshot itemSnapshot;

	@Column(nullable = false)
	private Integer quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@Setter(AccessLevel.PACKAGE)
	private Order order;

	private OrderItem(ItemSnapshot itemSnapshot, Integer quantity) {
		this.itemSnapshot = itemSnapshot;
		this.quantity = quantity;
	}

	public static OrderItem create(ItemSnapshot itemSnapshot, Integer quantity) {
		validateNullData(itemSnapshot, "주문 항목의 상품 스냅샷은 필수입니다.");
		validateNullData(quantity, "상품 주문 수량은 필수입니다.");
		if (quantity < MIN_QUANTITY || quantity > MAX_QUANTITY) {
			throw new OrderException(OrderExceptionMessage.INVALID_QUANTITY_COUNT);
		}
		return new OrderItem(itemSnapshot, quantity);
	}

	public BigDecimal calculateItemFinalPrice() {
		return this.itemSnapshot.getUnitPrice()
			.multiply(BigDecimal.valueOf(quantity));
	}
}
