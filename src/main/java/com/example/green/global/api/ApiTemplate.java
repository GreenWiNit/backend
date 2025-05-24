package com.example.green.global.api;

public record ApiTemplate<T>(
	boolean success,
	String message,
	T result
) {

	public static <T> ApiTemplate<T> ok(ResponseMessage responseMessage) {
		return new ApiTemplate<>(true, responseMessage.getMessage(), null);
	}

	public static <T> ApiTemplate<T> ok(ResponseMessage responseMessage, T result) {
		return new ApiTemplate<>(true, responseMessage.getMessage(), result);
	}
}
