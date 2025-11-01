package com.example.green.domain.dashboard.growth.message;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrowthExceptionMessage implements ExceptionMessage {

	WRONG_LEVEL_NAME(NOT_FOUND, "잘못된 레벨 이름입니다"),
	NOT_FOUND_USER(NOT_FOUND, "사용자를 찾을 수 없습니다"),
	INVALID_LEVEL(NOT_FOUND, "잘못된 레벨입니다");

	private final HttpStatus httpStatus;
	private final String message;
}
