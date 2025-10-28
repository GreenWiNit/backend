package com.example.green.domain.dashboard.rankingmodule.exception;

import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.ExceptionMessage;

public class WeeklyRankingException extends BusinessException {
	public WeeklyRankingException(ExceptionMessage exceptionMessage) {
		super(exceptionMessage);
	}
}
