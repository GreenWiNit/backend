package com.example.green.domain.auth.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PhoneExceptionMessage implements ExceptionMessage {

	INVALID_PHONE_NUMBER(BAD_REQUEST, "전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678"),
	OVER_MAX_TRY(UNAUTHORIZED, "최대 인증 횟수를 초과했습니다."),
	TOKEN_MISMATCH(UNAUTHORIZED, "전화번호 인증에 실패했습니다."),
	VERIFICATION_EXPIRED(UNAUTHORIZED, "전화번호 인증 유효 시간이 초과했습니다."),
	REQUIRES_VERIFY_REQUEST(BAD_REQUEST, "요청된 전화번호 인증이 없습니다."),
	NOT_FOUND_TOKEN(NOT_FOUND, "요청된 전화번호로 전송된 토큰 정보가 업습니다.");

	public static final String REQUIRES_PHONE_NUMBER = "전화번호는 필수 정보 입니다.";

	private final HttpStatus httpStatus;
	private final String message;
}
