package com.example.green.domain.info.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InfoExceptionMessage implements ExceptionMessage {
	INVALID_CATEGORY_CODE(BAD_REQUEST, "유효하지 않은 카테고리 코드입니다."),
	INVALID_INFO_NUMBER(BAD_REQUEST, "해당 정보 번호의 내용은 찾을 수 없습니다."),
	CANNOT_MAKE_INFO_ID(INTERNAL_SERVER_ERROR, "정보 ID 생성 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
