package com.example.green.domain.pointshop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.pointshop.controller.docs.DeliveryAddressControllerDocs;
import com.example.green.domain.pointshop.controller.dto.DeliveryAddressCreateDto;
import com.example.green.domain.pointshop.controller.message.DeliveryAddressResponseMessage;
import com.example.green.domain.pointshop.entity.delivery.vo.Address;
import com.example.green.domain.pointshop.entity.delivery.vo.Recipient;
import com.example.green.domain.pointshop.service.DeliveryAddressService;
import com.example.green.domain.pointshop.service.command.DeliveryAddressCreateCommand;
import com.example.green.domain.pointshop.service.result.DeliveryResult;
import com.example.green.global.api.ApiTemplate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries/address")
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

	@GetMapping
	public ApiTemplate<DeliveryResult> getDeliveryAddress() {
		// todo: security 추가 시 recipientId Resolver 로 받기
		DeliveryResult result = deliveryAddressService.getDeliveryAddress(1L);
		return ApiTemplate.ok(DeliveryAddressResponseMessage.DELIVERY_ADDRESS_GET_SUCCESS, result);
	}
}
