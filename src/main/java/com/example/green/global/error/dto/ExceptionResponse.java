package com.example.green.global.error.dto;

import com.example.green.global.error.exception.ExceptionMessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "공통 예외 응답")
@Getter
public class ExceptionResponse {

	@Schema(description = "성공 여부", example = "false")
	private final boolean success;

	@Schema(description = "에러 메시지", example = "유효하지 않은 요청입니다.")
	private final String message;

	public ExceptionResponse(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public static ExceptionResponse fail(ExceptionMessage exceptionMessage) {
		return new ExceptionResponse(false, exceptionMessage.getMessage());
	}
}
