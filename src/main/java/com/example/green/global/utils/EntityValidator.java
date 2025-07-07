package com.example.green.global.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class EntityValidator {

	public static void validateNullData(Object data, String message) {
		if (Objects.isNull(data)) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	public static void validateAutoIncrementId(Number number, String message) {
		if (Objects.isNull(number) || number.longValue() < 1L) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	public static void validateEmptyString(String string, String message) {
		if (string == null || string.isBlank()) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	public static void validateEmptyList(List<?> list, String message) {
		if (list == null || list.isEmpty()) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * 날짜 범위 검증 (시작일 <= 종료일)
	 */
	public static void validateDateRange(LocalDateTime beginDate, LocalDateTime endDate, String message) {
		if (Objects.isNull(beginDate) || Objects.isNull(endDate)) {
			log.error(message + " - 날짜가 null입니다.");
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
		if (beginDate.isAfter(endDate)) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * 양수 검증
	 */
	public static void validatePositiveNumber(Number number, String message) {
		if (Objects.isNull(number) || number.longValue() <= 0) {
			log.error(message);
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	/**
	 * 문자열 길이 범위 검증
	 */
	public static void validateStringLength(String string, int minLength, int maxLength, String message) {
		if (string == null || string.length() < minLength || string.length() > maxLength) {
			log.error(message + String.format(" (입력길이: %d, 허용범위: %d-%d)",
				string != null ? string.length() : 0, minLength, maxLength));
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}
}
