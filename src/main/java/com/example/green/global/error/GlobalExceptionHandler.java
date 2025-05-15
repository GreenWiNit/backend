package com.example.green.global.error;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.example.green.global.error.dto.ErrorSpot;
import com.example.green.global.error.dto.ExceptionResponse;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// 이유를 알 수 없는 에러
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.INTERNAL_SERVER_ERROR_MESSAGE);
	}

	// 존재 하지 않는 End-Point로 접근 시 발생하는 에러
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	// BeanValidation(jakarta.validation.constraints) 유효성 검증 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception
	) {
		log.error("{} : {}", exception.getClass(), extractErrorSpots(exception));
		if (hasTypeMismatch(exception)) {
			return buildExceptionResponse(GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE);
		}

		return buildExceptionResponse(GlobalExceptionMessage.ARGUMENT_NOT_VALID_MESSAGE);
	}

	// RequestParam, PathVariable Type Mismatch 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception
	) {
		log.error("{} : {}", exception.getClass(), extractErrorSpot(exception));
		return buildExceptionResponse(GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE);
	}

	// RequestParam 이 누락된 경우 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException exception
	) {
		log.error("{} : {}", exception.getClass(), extractErrorSpot(exception));
		return buildExceptionResponse(GlobalExceptionMessage.MISSING_PARAMETER_MESSAGE);
	}

	// 잘못된 Dto 정보에 대해 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception
	) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.DATA_NOT_READABLE_MESSAGE);
	}

	// contentType 이 잘못된 경우 발생하는 예외
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException exception
	) {
		log.error("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.UNSUPPORTED_MEDIA_TYPE_MESSAGE);
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

	private List<ErrorSpot> extractErrorSpots(MethodArgumentNotValidException exception) {
		return exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(fieldError -> new ErrorSpot(fieldError.getField(), fieldError.getDefaultMessage()))
			.toList();
	}

	private ErrorSpot extractErrorSpot(MethodArgumentTypeMismatchException exception) {
		final String type = exception.getRequiredType().getSimpleName();
		final String customMessage = " (으)로 변환할 수 없는 요청입니다.";
		return new ErrorSpot(exception.getName(), type + customMessage);
	}

	private static ErrorSpot extractErrorSpot(MissingServletRequestParameterException exception) {
		return new ErrorSpot(exception.getParameterName(), exception.getParameterType());
	}

	private boolean hasTypeMismatch(MethodArgumentNotValidException exception) {
		return exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.anyMatch(FieldError::isBindingFailure);
	}

}
