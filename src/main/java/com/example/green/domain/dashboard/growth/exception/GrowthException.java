package com.example.green.domain.dashboard.growth.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class GrowthException extends BusinessException {
	public GrowthException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
