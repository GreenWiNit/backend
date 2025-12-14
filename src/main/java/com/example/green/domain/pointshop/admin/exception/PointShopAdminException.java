package com.example.green.domain.pointshop.admin.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class PointShopAdminException extends BusinessException {

	public PointShopAdminException(ExceptionMessage message) {
		super(message);
	}
}
