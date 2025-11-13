package com.example.green.domain.dashboard.growth.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrowthResponseMessage implements ResponseMessage {

	LOAD_GROWTH_SUCCESS("사용자의 성장 데이터 조회에 성공했습니다"),
	CHANGE_APPLICABILITY("아이템 장착 여부가 변경되었습니다"),
	LOAD_ITEMS_SUCCESS("사용자의 아이템 조회에 성공했습니다");

	private final String message;

}
