package com.example.green.global.error.dto;

import java.util.List;

import com.example.green.global.error.exception.ExceptionMessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "상세 에러 정보 포함 예외 응답")
@Getter
public class DetailedExceptionResponse extends ExceptionResponse {

	@Schema(description = "상세 에러 정보")
	private final List<ErrorSpot> errors;

	public DetailedExceptionResponse(boolean success, String message, List<ErrorSpot> errors) {
		super(success, message);
		this.errors = errors;
	}

	public static DetailedExceptionResponse fail(ExceptionMessage exceptionMessage, List<ErrorSpot> errors) {
		return new DetailedExceptionResponse(false, exceptionMessage.getMessage(), errors);
	}

	public static DetailedExceptionResponse fail(ExceptionMessage exceptionMessage, ErrorSpot error) {
		return new DetailedExceptionResponse(false, exceptionMessage.getMessage(), List.of(error));
	}
}
