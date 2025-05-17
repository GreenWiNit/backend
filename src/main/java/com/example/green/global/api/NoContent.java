package com.example.green.global.api;

public record NoContent(boolean success, String message) {

	public static NoContent ok(ResponseMessage responseMessage) {
		return new NoContent(true, responseMessage.getMessage());
	}
}
