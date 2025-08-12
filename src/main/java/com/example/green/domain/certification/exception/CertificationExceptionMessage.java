package com.example.green.domain.certification.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;

@Getter
public enum CertificationExceptionMessage implements ExceptionMessage {

	EXISTS_TEAM_CHALLENGE_CERT_OF_DAY(HttpStatus.BAD_REQUEST, "해당 날짜의 팀 챌린지 인증이 이미 존재합니다."),
	FUTURE_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "인증 날짜는 미래 날짜를 선택할 수 없습니다."),
	INVALID_CHALLENGE_TYPE(HttpStatus.BAD_REQUEST, "챌린지 유형을 명확히 해주세요. (T or P)");

	private final HttpStatus httpStatus;
	private final String message;

	CertificationExceptionMessage(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
