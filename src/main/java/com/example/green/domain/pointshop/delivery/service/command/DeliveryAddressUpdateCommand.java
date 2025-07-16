package com.example.green.domain.pointshop.delivery.service.command;

import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;

public record DeliveryAddressUpdateCommand(
	Long recipientId,
	Long deliveryAddressId,
	Recipient recipient,
	Address address
) {

	public static DeliveryAddressUpdateCommand of(
		Long recipientId,
		Long deliveryAddressId,
		DeliveryAddressUpdateDto dto
	) {
		return new DeliveryAddressUpdateCommand(recipientId, deliveryAddressId, dto.toRecipient(), dto.toAddress());
	}
}
