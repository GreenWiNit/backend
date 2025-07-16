package com.example.green.domain.pointshop.order.entity.vo;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderDeliveryStatus {
	PENDING_DELIVERY("상품 신청"),
	SHIPPING("배송중"),
	DELIVERED("배송 완료"),
	CANCELLED("신청 취소");

	@JsonValue
	private final String value;
}
