package com.example.green.domain.info.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class InfoException extends BusinessException {

	public InfoException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}

}
