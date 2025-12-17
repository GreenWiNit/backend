package com.example.green.domain.pointshop.admin.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointShopAdminResponseMessage implements ResponseMessage {

	POINT_ITEM_CREATION_SUCCESS("포인트 상점 아이템 / 상품 생성에 성공했습니다."),
	POINT_ITEM_UPDATE_SUCCESS("포인트 상점 아이템 / 상품 수정에 성공했습니다."),
	POINT_ITEM_DELETE_SUCCESS("아이템 / 상품수정 삭제에 성공했습니다.");

	private final String message;
}
