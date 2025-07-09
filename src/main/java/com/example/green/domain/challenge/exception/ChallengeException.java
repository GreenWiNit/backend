package com.example.green.domain.challenge.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class ChallengeException extends BusinessException {

	public ChallengeException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
