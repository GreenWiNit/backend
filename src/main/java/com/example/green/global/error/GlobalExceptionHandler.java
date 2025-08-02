package com.example.green.global.error;

import static com.example.green.global.error.exception.GlobalExceptionMessage.*;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	// Spring Security 권한 예외 처리
	@ExceptionHandler(AuthorizationDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAuthorizationDeniedException(
		AuthorizationDeniedException exception) {
		log.error("{} : {}", exception.getClass().getSimpleName(), exception.getMessage());

		if (isUserAuthenticated()) {
			return buildExceptionResponse(GlobalExceptionMessage.ACCESS_DENIED_MESSAGE);
		} else {
			return buildExceptionResponse(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}
	}

	// 이유를 알 수 없는 에러 (fallback)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(Exception exception) {
		log.error("{} : {}", exception.getClass().getSimpleName(), exception.toString());
		return buildExceptionResponse(INTERNAL_SERVER_ERROR_MESSAGE);
	}

	// 존재하지 않는 End-Point로 접근 시 발생하는 에러
	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ExceptionResponse> handleNoResourceFoundException(NoResourceFoundException exception) {
		log.warn("{} : {}", exception.getClass().getSimpleName(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	// BeanValidation 유효성 검증 에러 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
		MethodArgumentNotValidException exception) {
		List<ErrorSpot> errorSpots = extractErrorSpots(exception);
		ExceptionMessage exceptionMessage = extractExceptionMessage(exception);
		log.warn("[{}] : {}", exception.getClass().getSimpleName(), errorSpots);

		return ResponseEntity.status(exceptionMessage.getHttpStatus())
			.body(DetailedExceptionResponse.fail(exceptionMessage, errorSpots));
	}

	// RequestParam, PathVariable Type Mismatch 에러 처리
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ExceptionResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException exception) {
		final String type = Optional.ofNullable(exception.getRequiredType())
			.map(Class::getSimpleName)
			.orElse("Unknown");
		final String customMessage = " (으)로 변환할 수 없는 요청입니다.";
		ErrorSpot errorSpot = new ErrorSpot(exception.getName(), type + customMessage);
		log.warn("{} : {}", exception.getClass().getSimpleName(), errorSpot);
		return ResponseEntity.status(GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE.getHttpStatus())
			.body(DetailedExceptionResponse.fail(GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE, errorSpot));
	}

	// RequestParam 이 누락된 경우 에러 처리
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(
		MissingServletRequestParameterException exception) {
		ErrorSpot errorSpot = new ErrorSpot(exception.getParameterName(), exception.getParameterType());
		log.warn("{} : {}", exception.getClass().getSimpleName(), errorSpot);
		return ResponseEntity.status(GlobalExceptionMessage.MISSING_PARAMETER_MESSAGE.getHttpStatus())
			.body(DetailedExceptionResponse.fail(GlobalExceptionMessage.MISSING_PARAMETER_MESSAGE, errorSpot));
	}

	// 잘못된 DTO 로직 (읽을 수 없는 메시지)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
		HttpMessageNotReadableException exception) {
		log.warn("{} : {}", exception.getClass().getSimpleName(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.DATA_NOT_READABLE_MESSAGE);
	}

	// contentType 이 잘못된 경우 발생하는 예외
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
		HttpMediaTypeNotSupportedException exception) {
		log.warn("[{}] : {}", exception.getClass().getSimpleName(), exception.getMessage());
		return buildExceptionResponse(GlobalExceptionMessage.UNSUPPORTED_MEDIA_TYPE_MESSAGE);
	}

	// 비즈니스 예외
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException exception) {
		log.error("{} {}", exception.extractExceptionLocation(), exception.getMessage());
		return buildExceptionResponse(exception.getExceptionMessage());
	}

	// ------------------------------------------------------------------
	// helpers

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
			return GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE;
		}
		return GlobalExceptionMessage.ARGUMENT_NOT_VALID_MESSAGE;
	}

	private boolean isUserAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return false;
		}
		Object principal = auth.getPrincipal();
		return !"anonymousUser".equals(principal);
	}
}