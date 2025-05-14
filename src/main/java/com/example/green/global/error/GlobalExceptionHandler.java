package com.example.green.global.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.green.global.error.dto.ExceptionResponse;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.INTERNAL_SERVER_ERROR_MESSAGE);
	}

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.NO_RESOURCE_EXCEPTION_MESSAGE);
	}

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleBadRequestException(BusinessException exception) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(exception.getExceptionMessage());
	}

	private ResponseEntity<ExceptionResponse> buildExceptionResponse(ExceptionMessage exceptionMessage) {
		return ResponseEntity.status(exceptionMessage.getHttpStatus())
			.body(ExceptionResponse.fail(exceptionMessage));
	}
}
