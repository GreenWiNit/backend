package com.example.green.domain.pointshop.delivery.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class DeliveryAddressException extends BusinessException {
	public DeliveryAddressException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
