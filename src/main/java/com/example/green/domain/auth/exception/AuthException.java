package com.example.green.domain.auth.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class AuthException extends BusinessException {
	public AuthException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
