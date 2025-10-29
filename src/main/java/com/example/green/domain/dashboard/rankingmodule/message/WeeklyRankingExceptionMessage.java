package com.example.green.domain.dashboard.rankingmodule.message;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeeklyRankingExceptionMessage implements ExceptionMessage {

	VALIDATION_NULL_OR_BLANK(BAD_REQUEST, "필수 항목이 누락되었습니다."),
	NEGATIVE_NUMBER_NOT_ALLOWED(BAD_REQUEST, "음수 값은 입력할수없습니다"),

	NOT_FOUND_USER(NOT_FOUND, "사용자를 찾을 수 없습니다");

	private final HttpStatus httpStatus;
	private final String message;
}
