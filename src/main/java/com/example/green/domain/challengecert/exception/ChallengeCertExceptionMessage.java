package com.example.green.domain.challengecert.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;

@Getter
public enum ChallengeCertExceptionMessage implements ExceptionMessage {

	INVALID_MEMBER_SEQUENCE(HttpStatus.BAD_REQUEST, "멤버 가입 순서는 1 이상이어야 하며, 일반 멤버는 1번이 될 수 없습니다."),
	CERTIFICATION_ALREADY_APPROVED(HttpStatus.BAD_REQUEST, "이미 승인된 인증입니다."),
	CERTIFICATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "해당 날짜에 이미 인증이 존재합니다."),
	INVALID_CERTIFICATION_DATE(HttpStatus.BAD_REQUEST, "인증 날짜는 미래 날짜를 선택할 수 없습니다."),
	DUPLICATE_TEAM_LEADER(HttpStatus.BAD_REQUEST, "이미 리더가 있는 팀에 리더를 추가할 수 없습니다."),
	CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "인증을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;

	ChallengeCertExceptionMessage(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
