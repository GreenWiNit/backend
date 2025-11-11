package com.example.green.domain.pointshop.item.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointItemResponseMessage implements ResponseMessage {

	POINT_ITEM_CREATION_SUCCESS("포인트 상점 아이템 생성에 성공했습니다."),
	POINT_ITEM_UPDATE_SUCCESS("포인트 상점 아이템 수정에 성공했습니다."),
	POINT_ITEMS_INQUIRY_SUCCESS("아이템 목록 조회에 성공했습니다."),
	POINT_ITEM_DETAIL_INQUIRY_SUCCESS("아이템 상세 조회에 성공했습니다."),
	POINT_ITEM_LOAD_SUCCESS("아이템 조회에 성공했습니다"),
	DISPLAY_SHOW_ITEM_SUCCESS("아이템이 전시 상태가 됐습니다."),
	POINT_ITEM_DELETE_SUCCESS("아이템 삭제에 성공했습니다."),
	DISPLAY_HIDE_ITEM_SUCCESS("아이템이 미전시 상태가 됐습니다."),
	POINT_ITEM_ORDER_SUCCESS("아이템 교환이 완료되었습니다");

	private final String message;

}
