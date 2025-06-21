package com.example.green.domain.info.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InfoExceptionMessage implements ExceptionMessage {
	INVALID_CATEGORY_CODE(BAD_REQUEST, "유효하지 않은 카테고리 코드입니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
