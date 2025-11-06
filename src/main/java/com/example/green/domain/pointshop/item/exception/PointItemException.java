package com.example.green.domain.pointshop.item.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class PointItemException extends BusinessException {

	public PointItemException(ExceptionMessage message) {
		super(message);
	}
}
