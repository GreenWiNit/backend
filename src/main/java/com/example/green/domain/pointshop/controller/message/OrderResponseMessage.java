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
	;

	private final String message;
}
