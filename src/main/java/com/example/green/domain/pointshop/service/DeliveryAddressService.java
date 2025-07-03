package com.example.green.domain.pointshop.service;

import static com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressExceptionMessage.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;
import com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressException;
import com.example.green.domain.pointshop.repository.DeliveryAddressRepository;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;
import com.example.green.domain.pointshop.service.result.DeliveryResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

	private final DeliveryAddressRepository deliveryAddressRepository;

	@Transactional
	public Long saveSingleAddress(DeliveryAddressCreateCommand command) {
		// todo: 전화번호 인증 확인하기
		validateExistsDeliveryAddress(command.recipientId());
		DeliveryAddress deliveryAddress = DeliveryAddress.create(
			command.recipientId(),
			command.recipient(),
			command.address()
		);
		DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);

		return saved.getId();
	}

	private void validateExistsDeliveryAddress(Long recipientId) {
		if (deliveryAddressRepository.existsByRecipientId(recipientId)) {
			throw new DeliveryAddressException(DELIVERY_ADDRESS_ALREADY_EXISTS);
		}
	}

	public DeliveryResult getDeliveryAddress(Long recipientId) {
		return deliveryAddressRepository.findByRecipientId(recipientId)
			.map(deliveryAddress -> DeliveryResult.of(
				deliveryAddress.getRecipientId(),
				deliveryAddress.getRecipient(),
				deliveryAddress.getAddress())
			)
			.orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_DELIVERY_ADDRESS));
	}
}
