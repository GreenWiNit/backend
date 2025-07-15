package com.example.green.domain.mypage.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MypageExceptionMessage implements ExceptionMessage {
	NULL_USER_TOTAL_POINTS(BAD_REQUEST, "사용자의 총 포인트는 NULL 일 수 없습니다."),;

	private final HttpStatus httpStatus;
	private final String message;
}
