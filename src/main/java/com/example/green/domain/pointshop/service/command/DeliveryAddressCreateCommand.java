package com.example.green.domain.pointshop.service.command;

import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;

public record DeliveryAddressCreateCommand(
	Long recipientId,
	Recipient recipient,
	Address address
) {

	public DeliveryAddress toDeliveryAddress() {
		return DeliveryAddress.create(recipientId, recipient, address);
	}
}
