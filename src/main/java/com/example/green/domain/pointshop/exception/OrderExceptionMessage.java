package com.example.green.domain.pointshop.exception;

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

	private final HttpStatus httpStatus;
	private final String message;
}
