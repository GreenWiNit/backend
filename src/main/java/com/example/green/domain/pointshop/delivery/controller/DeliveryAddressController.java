package com.example.green.domain.pointshop.delivery.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.delivery.controller.dto.DeliveryAddressUpdateDto;
import com.example.green.domain.pointshop.delivery.entity.vo.Address;
import com.example.green.domain.pointshop.delivery.entity.vo.Recipient;
import com.example.green.domain.pointshop.delivery.service.DeliveryAddressService;
import com.example.green.domain.pointshop.delivery.service.command.DeliveryAddressCreateCommand;
import com.example.green.domain.pointshop.delivery.service.command.DeliveryAddressUpdateCommand;
import com.example.green.domain.pointshop.delivery.service.result.DeliveryResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries/addresses")
public class DeliveryAddressController implements DeliveryAddressControllerDocs {

	private final DeliveryAddressService deliveryAddressService;

	@PostMapping
	public ApiTemplate<Long> createDeliveryAddress(
		@Valid @RequestBody DeliveryAddressCreateDto dto
	) {
		Recipient recipient = Recipient.of(dto.recipientName(), dto.phoneNumber());
		Address address = Address.of(dto.roadAddress(), dto.detailAddress(), dto.zipCode());

		// todo: security 추가 시 recipientId Resolver 로 받기
		DeliveryAddressCreateCommand command = new DeliveryAddressCreateCommand(1L, recipient, address);
		Long result = deliveryAddressService.saveSingleAddress(command);

		return ApiTemplate.ok(DeliveryAddressResponseMessage.DELIVERY_ADDRESS_ADD_SUCCESS, result);
	}

	@PutMapping("/{deliveryAddressId}")
	public NoContent updateDeliveryAddress(
		@Valid @RequestBody DeliveryAddressUpdateDto dto,
		@PathVariable Long deliveryAddressId
	) {
		DeliveryAddressUpdateCommand command = DeliveryAddressUpdateCommand.of(1L, deliveryAddressId, dto);
		deliveryAddressService.updateSingleAddress(command);
		return NoContent.ok(DeliveryAddressResponseMessage.DELIVERY_ADDRESS_UPDATE_SUCCESS);
	}

	@GetMapping
	public ApiTemplate<DeliveryResult> getDeliveryAddress() {
		// todo: security 추가 시 recipientId Resolver 로 받기
		DeliveryResult result = deliveryAddressService.getDeliveryAddressByRecipient(1L);
		return ApiTemplate.ok(DeliveryAddressResponseMessage.DELIVERY_ADDRESS_GET_SUCCESS, result);
	}
}
