package com.example.green.global.utils;

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
}
