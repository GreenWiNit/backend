package com.example.green.global.error.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private final ExceptionMessage exceptionMessage;
	private final String className;
	private final String methodName;
	private final int lineNumber;

	public BusinessException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage.getMessage());
		this.exceptionMessage = exceptionMessage;

		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		this.className = stack[2].getClassName();
		this.methodName = stack[2].getMethodName();
		this.lineNumber = stack[2].getLineNumber();
	}

	public String extractExceptionLocation() {
		return String.format("[%s][%s][%d]: ", className, methodName, lineNumber);
	}
}
