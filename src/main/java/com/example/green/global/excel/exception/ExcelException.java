package com.example.green.global.excel.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class ExcelException extends BusinessException {
	public ExcelException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
