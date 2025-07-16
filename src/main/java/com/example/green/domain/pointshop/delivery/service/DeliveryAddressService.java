package com.example.green.domain.pointshop.delivery.service;

import static com.example.green.domain.pointshop.delivery.exception.DeliveryAddressExceptionMessage.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.pointshop.delivery.entity.DeliveryAddress;
import com.example.green.domain.pointshop.delivery.exception.DeliveryAddressException;
import com.example.green.domain.pointshop.delivery.repository.DeliveryAddressRepository;
import com.example.green.domain.pointshop.delivery.service.command.DeliveryAddressCreateCommand;
import com.example.green.domain.pointshop.delivery.service.command.DeliveryAddressUpdateCommand;
import com.example.green.domain.pointshop.delivery.service.result.DeliveryResult;
import com.example.green.domain.pointshop.order.entity.vo.DeliveryAddressSnapshot;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryAddressService {

	private final DeliveryAddressRepository deliveryAddressRepository;

	@Transactional
	public Long saveSingleAddress(DeliveryAddressCreateCommand command) {
		validateExistsDeliveryAddress(command.recipientId());
		DeliveryAddress deliveryAddress = command.toDeliveryAddress();
		DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);

		return saved.getId();
	}

	private void validateExistsDeliveryAddress(Long recipientId) {
		if (deliveryAddressRepository.existsByRecipientId(recipientId)) {
			throw new DeliveryAddressException(DELIVERY_ADDRESS_ALREADY_EXISTS);
		}
	}

	public DeliveryResult getDeliveryAddressByRecipient(Long recipientId) {
		return deliveryAddressRepository.findByRecipientId(recipientId)
			.map(deliveryAddress -> DeliveryResult.of(
				deliveryAddress.getRecipientId(),
				deliveryAddress.getRecipient(),
				deliveryAddress.getAddress())
			)
			.orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_DELIVERY_ADDRESS));
	}

	// todo: 도메인 컨텍스트 분리 시 관심사 분리
	public DeliveryAddressSnapshot getSnapshot(Long deliveryAddressId) {
		return deliveryAddressRepository.findById(deliveryAddressId)
			.map(deliveryAddress -> DeliveryAddressSnapshot.of(
				deliveryAddress.getId(),
				deliveryAddress.getRecipient().getRecipientName(),
				deliveryAddress.getRecipient().getPhoneNumber(),
				deliveryAddress.getAddress().getRoadAddress(),
				deliveryAddress.getAddress().getDetailAddress(),
				deliveryAddress.getAddress().getZipCode()
			))
			.orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_DELIVERY_ADDRESS));
	}

	public void validateAddressOwnership(Long deliveryAddressId, Long recipientId) {
		if (!deliveryAddressRepository.existsByIdAndRecipientId(deliveryAddressId, recipientId)) {
			throw new DeliveryAddressException(INVALID_OWNERSHIP);
		}
	}

	public void updateSingleAddress(DeliveryAddressUpdateCommand command) {
		DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(command.deliveryAddressId())
			.orElseThrow(() -> new DeliveryAddressException(NOT_FOUND_DELIVERY_ADDRESS));

		deliveryAddress.updateRecipient(command.recipient());
		deliveryAddress.updateAddress(command.address());
	}
}
