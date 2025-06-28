package com.example.green.global.utils;

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
		log.error(message);
		if (Objects.isNull(data)) {
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	public static void validateAutoIncrementId(Number number, String message) {
		log.error(message);
		if (Objects.isNull(number) || number.longValue() < 1L) {
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}

	public static void validateEmptyString(String string, String message) {
		log.error(message);
		if (string == null || string.isBlank()) {
			throw new BusinessException(GlobalExceptionMessage.UNPROCESSABLE_ENTITY);
		}
	}
}
