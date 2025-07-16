package com.example.green.domain.pointshop.order.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderExceptionMessage implements ExceptionMessage {

	INVALID_QUANTITY_COUNT(BAD_REQUEST, "상품 수량은 최소 1개부터 최대 5개까지 선택할 수 있습니다."),
	NO_PENDING_STATUS(BAD_REQUEST, "배송 대기 상태가 아닙니다."),
	NO_SHIPPING_STATUS(BAD_REQUEST, "배송 상태가 아닙니다."),
	NOT_FOUND_ORDER(NOT_FOUND, "주문 정보를 찾을 수 없습니다.");

	public static final String REQUIRE_DELIVERY_ADDRESS = "상품 주문 시 배송지 정보는 필수 값입니다.";
	public static final String REQUIRE_DELIVERY_ADDRESS_ID = "배송지 식별자는 필수 값 입니다.";
	public static final String REQUIRE_RECIPIENT_NAME = "수령자 정보는 필수 값 입니다.";
	public static final String REQUIRE_RECIPIENT_PHONE_NUMBER = "배송자 전화번호 정보는 필수 값 입니다.";
	public static final String REQUIRE_ROAD_ADDRESS = "도로명 주소는 필수 값 입니다.";
	public static final String REQUIRE_DETAIL_ADDRESS = "상세 주소는 필수 값 입니다.";
	public static final String REQUIRE_ZIP_CODE = "우편 번호는 필수 값 입니다.";
	public static final String REQUIRE_MEMBER_SNAPSHOT = "상품 주문 시 주문자 정보는 필수 값 입니다.";
	public static final String REQUIRE_ORDER_ITEM = "상품 주문시 상품 정보는 1개 이상 필요합니다.";

	private final HttpStatus httpStatus;
	private final String message;
}
