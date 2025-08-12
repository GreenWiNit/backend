package com.example.green.domain.certification.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class CertificationException extends BusinessException {

	public CertificationException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
