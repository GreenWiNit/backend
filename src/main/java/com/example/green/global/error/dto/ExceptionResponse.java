package com.example.green.global.error.dto;

import com.example.green.global.error.exception.ExceptionMessage;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 예외 응답")
public record ExceptionResponse(
	@Schema(description = "성공 여부", example = "false")
	boolean success,

	@Schema(description = "에러 메시지", example = "유효하지 않은 요청입니다.")
	String message
) {
	public static ExceptionResponse fail(ExceptionMessage exceptionMessage) {
		return new ExceptionResponse(false, exceptionMessage.getMessage());
	}

	public static ExceptionResponse fail(String message) {
		return new ExceptionResponse(false, message);
	}
}
