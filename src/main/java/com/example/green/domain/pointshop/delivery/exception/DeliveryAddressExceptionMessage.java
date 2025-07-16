package com.example.green.domain.pointshop.delivery.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryAddressExceptionMessage implements ExceptionMessage {

	INVALID_PHONE_NUMBER(BAD_REQUEST, "올바르지 않은 전화번호 형식입니다. (예: 010-1234-5678)"),
	INVALID_ZIP_CODE(BAD_REQUEST, "우편번호는 5자리 숫자여야 합니다. (예: 12345)"),
	DELIVERY_ADDRESS_ALREADY_EXISTS(CONFLICT, "이미 배송지 정보가 존재합니다."),
	NOT_FOUND_DELIVERY_ADDRESS(NOT_FOUND, "배송지 정보를 찾을 수 없습니다."),
	INVALID_OWNERSHIP(UNAUTHORIZED, "해당 배송지의 소유자가 아닙니다."),
	UN_AUTHORIZE_PHONE_NUMBER(UNAUTHORIZED, "휴대전화 인증 내역이 없습니다.");

	public static final String REQUIRE_RECIPIENT_ID = "물품 수령자 식별자는 필수 값 입니다.";
	public static final String REQUIRE_RECIPIENT_NAME = "물품 수령자 이름은 필수 값 입니다.";
	public static final String REQUIRE_RECIPIENT_PHONE_NUMBER = "물품 수령자 전화 번호는 필수 값 입니다.";
	public static final String REQUIRE_RECIPIENT = "물품 수령자 정보는 필수 값 입니다.";
	public static final String REQUIRE_ADDRESS = "물품 수령 주소 정보는 필수 값 입니다.";
	public static final String REQUIRE_ROAD_ADDRESS = "도로명 주소는 필수 값 입니다.";
	public static final String REQUIRE_DETAIL_ADDRESS = "상세 주소는 필수 값 입니다.";
	public static final String REQUIRE_ZIP_CODE = "우편 번호는 필수 값 입니다.";

	private final HttpStatus httpStatus;
	private final String message;
}
