package com.example.green.domain.pointshop.exception.point;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointExceptionMessage implements ExceptionMessage {

	INVALID_POINT_AMOUNT(BAD_REQUEST, "잘못된 포인트 금액입니다."),
	NOT_ENOUGH_POINT(BAD_REQUEST, "사용 가능한 포인트가 부족합니다."),
	NO_POINTS_ACCUMULATED(BAD_REQUEST, "적립한 포인트 내역이 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
