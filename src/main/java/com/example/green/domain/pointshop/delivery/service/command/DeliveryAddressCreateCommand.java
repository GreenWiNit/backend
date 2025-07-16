package com.example.green.domain.pointshop.delivery.service.command;

import com.example.green.domain.pointshop.delivery.entity.DeliveryAddress;
import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;

public record DeliveryAddressCreateCommand(
	Long recipientId,
	Recipient recipient,
	Address address
) {

	public DeliveryAddress toDeliveryAddress() {
		return DeliveryAddress.create(recipientId, recipient, address);
	}
}
