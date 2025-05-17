package com.example.green.global.error.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionMessage {

	HttpStatus getHttpStatus();

	String getMessage();
}
