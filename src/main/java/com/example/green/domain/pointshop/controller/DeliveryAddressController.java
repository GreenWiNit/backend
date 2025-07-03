package com.example.green.domain.pointshop.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.controller.message.DeliveryAddressResponseMessage;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;
import com.example.green.domain.pointshop.service.DeliveryAddressService;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries/address")
public class DeliveryAddressController {

	private final DeliveryAddressService deliveryAddressService;

	@PostMapping
	@AuthenticatedApi
	public ApiTemplate<Long> createDeliveryAddress(
		@Valid @RequestBody DeliveryAddressCreateDto dto
	) {
		Recipient recipient = Recipient.of(dto.recipientName(), dto.phoneNumber());
		Address address = Address.of(dto.roadAddress(), dto.detailAddress(), dto.zipCode());

		// todo: security 추가 시 recipientId Resolver 로 받기
		DeliveryAddressCreateCommand command = new DeliveryAddressCreateCommand(1L, recipient, address);
		Long result = deliveryAddressService.saveForSingleAddress(command);

		return ApiTemplate.ok(DeliveryAddressResponseMessage.DELIVERY_ADDRESS_ADD_SUCCESS, result);
	}
}
