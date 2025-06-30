package com.example.green.domain.pointshop.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class OrderException extends BusinessException {

	public OrderException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
