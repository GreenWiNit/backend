package com.example.green.domain.pointshop.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointProductResponseMessage implements ResponseMessage {

	POINT_PRODUCT_CREATION_SUCCESS("포인트 상품 생성에 성공했습니다."),
	POINT_PRODUCTS_INQUIRY_SUCCESS("포인트 상품 목록 조회에 성공했습니다."),
	POINT_PRODUCT_DETAIL_INQUIRY_SUCCESS("포인트 상품 상세 조회에 성공했습니다."),
	POINT_PRODUCT_UPDATE_SUCCESS("포인트 상품 수정에 성공했습니다."),
	POINT_PRODUCT_DELETE_SUCCESS("포인트 상품 삭제에 성공했습니다."),
	DISPLAY_SHOW_SUCCESS("포인트 상품이 전시 상태가 됐습니다."),
	DISPLAY_HIDE_SUCCESS("포인트 상품이 미전시 상태가 됐습니다.");

	private final String message;
}
