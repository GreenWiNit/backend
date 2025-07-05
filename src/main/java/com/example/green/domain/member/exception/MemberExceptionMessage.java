package com.example.green.domain.member.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberExceptionMessage implements ExceptionMessage {

	MEMBER_NOT_FOUND(NOT_FOUND, "해당 회원을 찾을 수 없습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
