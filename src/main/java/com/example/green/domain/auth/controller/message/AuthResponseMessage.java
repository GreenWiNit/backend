package com.example.green.domain.auth.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthResponseMessage implements ResponseMessage {

	SIGNUP_SUCCESS("회원가입이 성공적으로 완료되었습니다.");

	private final String message;
}