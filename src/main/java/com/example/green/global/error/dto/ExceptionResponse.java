package com.example.green.global.error.dto;

import com.example.green.global.error.exception.ExceptionMessage;

public record ExceptionResponse(
	boolean success,
	String message
) {

	public static ExceptionResponse fail(ExceptionMessage exceptionMessage) {
		return new ExceptionResponse(false, exceptionMessage.getMessage());
	}
}
