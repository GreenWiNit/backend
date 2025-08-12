package com.example.green.domain.certification.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;

@Getter
public enum CertificationExceptionMessage implements ExceptionMessage {

	EXISTS_TEAM_CHALLENGE_CERT_OF_DAY(HttpStatus.BAD_REQUEST, "해당 날짜의 팀 챌린지 인증이 이미 존재합니다."),
	FUTURE_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "인증 날짜는 미래 날짜를 선택할 수 없습니다."),
	DUPLICATE_TEAM_LEADER(HttpStatus.BAD_REQUEST, "이미 리더가 있는 팀에 리더를 추가할 수 없습니다."),
	CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증을 찾을 수 없습니다."),
	INVALID_CERTIFICATION_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 상태입니다.");

	private final HttpStatus httpStatus;
	private final String message;

	CertificationExceptionMessage(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
