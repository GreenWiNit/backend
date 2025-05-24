package com.example.green.domain.file.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class FileException extends BusinessException {

	public FileException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
