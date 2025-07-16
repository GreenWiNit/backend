package com.example.green.domain.pointshop.service.command;

import com.example.green.domain.pointshop.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;

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
