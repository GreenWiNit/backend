package com.example.green.domain.point.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PointExceptionMessage implements ExceptionMessage {

	INVALID_POINT_AMOUNT(BAD_REQUEST, "잘못된 포인트 금액입니다."),
	NOT_ENOUGH_POINT(BAD_REQUEST, "사용 가능한 포인트가 부족합니다.");

	public static final String REQUIRE_MEMBER_ID = "포인트 이력에서 사용자 ID는 필수 값 입니다.";
	public static final String REQUIRE_POINT_SOURCE = "포인트 출처는 필수 값 입니다. ";
	public static final String REQUIRE_POINT_SOURCE_ID = "포인트 출처의 식별자는 필수 값 입니다. ";
	public static final String REQUIRE_POINT_SOURCE_DESCRIPTION = "포인트 출처 설명은 필수 값 입니다.";
	public static final String REQUIRE_POINT_SOURCE_TYPE = "포인트 출처 종류는 필수 값 입니다. ";
	public static final String REQUIRE_POINT_AMOUNT = "포인트 금액은 필수 값 입니다.";

	private final HttpStatus httpStatus;
	private final String message;
}
