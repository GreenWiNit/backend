package com.example.green.global.api;

public record ApiResponse<T>(
	boolean success,
	String message,
	T result
) {

	public static <T> ApiResponse<T> ok(ResponseMessage responseMessage, T result) {
		return new ApiResponse<>(true, responseMessage.getMessage(), result);
	}
}
