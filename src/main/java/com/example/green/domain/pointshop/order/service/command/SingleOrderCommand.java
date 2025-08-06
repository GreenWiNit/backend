package com.example.green.domain.pointshop.order.service.command;

import com.example.green.domain.pointshop.order.entity.vo.MemberSnapshot;

public record SingleOrderCommand(
	MemberSnapshot memberSnapshot,
	Long deliveryAddressId,
	Long orderItemId,
	Integer quantity
) {
}
