package com.example.green.domain.mypage.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class MypageException extends BusinessException {
	public MypageException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
