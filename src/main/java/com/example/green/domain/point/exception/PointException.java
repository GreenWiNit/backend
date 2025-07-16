package com.example.green.domain.point.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class PointException extends BusinessException {
	public PointException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
