package com.example.green.domain.auth.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhoneVerificationResponseMessage implements ResponseMessage {

	PHONE_VERIFICATION_REQUEST_SUCCESS("휴대전화 인증 요청에 성공했습니다."),
	;

	private final String message;
}
