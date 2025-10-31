package com.example.green.domain.dashboard.growth.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GrowthResponseMessage implements ResponseMessage {

	LOAD_GROWTH_SUCCESS("사용자의 성장 데이터 조회에 성공했습니다");

	private final String message;

}
