package com.example.green.domain.pointshop.exception.deliveryaddress;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryAddressExceptionMessage implements ExceptionMessage {

	INVALID_PHONE_NUMBER(BAD_REQUEST, "올바르지 않은 전화번호 형식입니다. (예: 010-1234-5678)"),
	INVALID_ZIP_CODE(BAD_REQUEST, "우편번호는 5자리 숫자여야 합니다. (예: 12345)");

	private final HttpStatus httpStatus;
	private final String message;
}
