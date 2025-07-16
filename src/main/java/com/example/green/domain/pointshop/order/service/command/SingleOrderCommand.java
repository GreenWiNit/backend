package com.example.green.domain.pointshop.order.service.command;

import com.example.green.domain.pointshop.order.controller.dto.SingleOrderRequest;

public record SingleOrderCommand(
	Long memberId,
	String memberCode,
	Long deliveryAddressId,
	Long orderItemId,
	Integer quantity
) {

	public static SingleOrderCommand of(Long memberId, String memberCode, SingleOrderRequest request) {
		return new SingleOrderCommand(
			memberId,
			memberCode,
			request.deliveryAddressId(),
			request.orderItemId(),
			request.quantity()
		);
	}
}
