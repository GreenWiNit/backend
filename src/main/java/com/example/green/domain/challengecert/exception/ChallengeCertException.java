package com.example.green.domain.challengecert.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class ChallengeCertException extends BusinessException {

	public ChallengeCertException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
