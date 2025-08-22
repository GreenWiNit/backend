package com.example.green.domain.auth.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthResponseMessage implements ResponseMessage {

	SIGNUP_SUCCESS("회원가입이 성공적으로 완료되었습니다."),
	TOKEN_REFRESHED("토큰이 성공적으로 갱신되었습니다."),
	LOGOUT_SUCCESS("로그아웃이 완료되었습니다."),
	LOGOUT_ALL_SUCCESS("모든 디바이스에서 로그아웃이 완료되었습니다.");

	private final String message;
}