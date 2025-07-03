package com.example.green.domain.pointshop.service.result;

import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;

public record DeliveryResult(
	Long deliveryAddressId,
	String recipientName,
	String phoneNumber,
	String roadAddress,
	String detailAddress,
	String zipCode
) {

	public static DeliveryResult of(Long deliveryAddressId, Recipient recipient, Address address) {
		return new DeliveryResult(
			deliveryAddressId,
			recipient.getRecipientName(),
			recipient.getPhoneNumber(),
			address.getRoadAddress(),
			address.getDetailAddress(),
			address.getZipCode()
		);
	}
}
