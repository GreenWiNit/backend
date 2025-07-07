package com.example.green.domain.mypage.controller.api;

import com.example.green.global.api.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MypageResponseMessage implements ResponseMessage {
	GET_MYPAGE_MAIN_SUCCESS("마이페이지 메인 조회 성공"),
	;
	private final String Message;
}
