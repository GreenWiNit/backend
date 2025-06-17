package com.example.green.global.error.dto;

public record ErrorSpot(String fieldName, String message) {

	private static final String MESSAGE_FORMAT = "필드명: %s, 예외 메세지: %s%n";

	@Override
	public String toString() {
		return String.format(MESSAGE_FORMAT, fieldName, message);
	}
}
