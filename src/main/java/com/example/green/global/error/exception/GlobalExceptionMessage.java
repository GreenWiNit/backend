package com.example.green.global.error.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GlobalExceptionMessage implements ExceptionMessage {

	INTERNAL_SERVER_ERROR_MESSAGE(INTERNAL_SERVER_ERROR, "알 수 없는 서버 에러가 발생했습니다."),
	NO_RESOURCE_EXCEPTION_MESSAGE(NOT_FOUND, "존재하지 않는 API 입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
