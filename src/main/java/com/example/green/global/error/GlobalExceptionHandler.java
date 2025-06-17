package com.example.green.global.error;

import static com.example.green.global.error.exception.GlobalExceptionMessage.*;

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

import com.example.green.global.error.dto.DetailedExceptionResponse;
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
		log.error("{} : {}", exception.getClass(), exception.toString());
		return buildExceptionResponse(INTERNAL_SERVER_ERROR_MESSAGE);
	}

	// 존재 하지 않는 End-Point로 접근 시 발생하는 에러
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.warn("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(NO_RESOURCE_MESSAGE);
	}

	// BeanValidation(jakarta.validation.constraints) 유효성 검증 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception
	) {
		List<ErrorSpot> errorSpots = extractErrorSpots(exception);
		ExceptionMessage exceptionMessage = extractExceptionMessage(exception);
		log.warn("[{}] : {}", exception.getClass(), errorSpots);

		return ResponseEntity.status(exceptionMessage.getHttpStatus())
			.body(DetailedExceptionResponse.fail(exceptionMessage, errorSpots));
	}

	// RequestParam, PathVariable Type Mismatch 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception
	) {
		final String type = exception.getRequiredType().getSimpleName();
		final String customMessage = " (으)로 변환할 수 없는 요청입니다.";
		ErrorSpot errorSpot = new ErrorSpot(exception.getName(), type + customMessage);
		log.warn("{} : {}", exception.getClass(), errorSpot);
		return ResponseEntity.status(ARGUMENT_TYPE_MISMATCH_MESSAGE.getHttpStatus())
			.body(DetailedExceptionResponse.fail(ARGUMENT_TYPE_MISMATCH_MESSAGE, errorSpot));
	}

	// RequestParam 이 누락된 경우 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException exception
	) {
		ErrorSpot errorSpot = new ErrorSpot(exception.getParameterName(), exception.getParameterType());
		log.warn("{} : {}", exception.getClass(), errorSpot);
		return ResponseEntity.status(MISSING_PARAMETER_MESSAGE.getHttpStatus())
			.body(DetailedExceptionResponse.fail(MISSING_PARAMETER_MESSAGE, errorSpot));
	}

	// 잘못된 Dto 정보에 대해 에러 처리
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception
	) {
		log.warn("{} : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.DATA_NOT_READABLE_MESSAGE);
	}

	// contentType 이 잘못된 경우 발생하는 예외
	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException exception
	) {
		log.warn("[{}] : {}", exception.getClass(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.UNSUPPORTED_MEDIA_TYPE_MESSAGE);
	}

	@ExceptionHandler
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exception) {
		log.error("{} {}", exception.extractExceptionLocation(), exception.getMessage());
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

	private ExceptionMessage extractExceptionMessage(MethodArgumentNotValidException exception) {
		boolean hasTypeMismatch = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.anyMatch(FieldError::isBindingFailure);

		if (hasTypeMismatch) {
			return ARGUMENT_TYPE_MISMATCH_MESSAGE;
		}
		return ARGUMENT_NOT_VALID_MESSAGE;
	}
}
