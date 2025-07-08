package com.example.green.domain.admin.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AdminExceptionMessage implements ExceptionMessage {

	ADMIN_NOT_FOUND(NOT_FOUND, "해당 관리자를 찾을 수 없습니다."),
	INVALID_PASSWORD(UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
	ADMIN_INACTIVE(FORBIDDEN, "비활성화된 관리자 계정입니다.");

	private final HttpStatus httpStatus;
	private final String message;
} 