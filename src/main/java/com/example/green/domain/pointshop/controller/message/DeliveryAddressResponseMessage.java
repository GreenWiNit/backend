package com.example.green.domain.pointshop.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DeliveryAddressResponseMessage implements ResponseMessage {

	DELIVERY_ADDRESS_ADD_SUCCESS("배송지 정보 추가에 성공했습니다."),
	DELIVERY_ADDRESS_GET_SUCCESS("배송지 조회에 성공했습니다.");

	private final String message;
}
