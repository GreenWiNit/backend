package com.example.green.domain.pointshop.product.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class PointProductException extends BusinessException {

	public PointProductException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
