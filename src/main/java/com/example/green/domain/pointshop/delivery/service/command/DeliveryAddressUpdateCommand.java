package com.example.green.domain.pointshop.delivery.service.command;

import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;

public record DeliveryAddressUpdateCommand(
	Long recipientId,
	Recipient recipient,
	Address address
) {

	public static DeliveryAddressUpdateCommand of(
		Long recipientId,
		DeliveryAddressUpdateDto dto
	) {
		return new DeliveryAddressUpdateCommand(recipientId, dto.toRecipient(), dto.toAddress());
	}
}
