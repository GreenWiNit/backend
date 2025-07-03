package com.example.green.domain.pointshop.service;

import static com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressExceptionMessage.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.entity.delivery.DeliveryAddress;
import com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressException;
import com.example.green.domain.pointshop.repository.DeliveryAddressRepository;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {

	private final DeliveryAddressRepository deliveryAddressRepository;

	@Transactional
	public Long saveForSingleAddress(DeliveryAddressCreateCommand command) {
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
}
