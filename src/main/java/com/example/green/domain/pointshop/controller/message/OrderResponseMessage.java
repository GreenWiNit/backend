package com.example.green.domain.pointshop.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum OrderResponseMessage implements ResponseMessage {

	POINT_PRODUCT_EXCHANGE_SUCCESS("단일 상품 주문에 성공했습니다."),
	EXCHANGE_APPLICANT_INQUIRY_SUCCESS("상품 교환 신청자 목록 조회에 성공했습니다."),
	EXCHANGE_APPLICATION_INQUIRY_SUCCESS("상품 교환 신쳥 목록 조회에 성공했습니다."),
	ORDER_SHIPPING_START_SUCCESS("주문이 배송 시작 상태로 변경되었습니다."),
	ORDER_DELIVERY_COMPLETE_SUCCESS("주문이 배송 완료 상태로 변경되었습니다.");

	private final String message;
}
