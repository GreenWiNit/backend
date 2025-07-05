package com.example.green.domain.pointshop.controller.dto;

public record SingleOrderRequest(
	Long deliveryAddressId,
	Long orderItemId,
	Integer quantity
) {
}
