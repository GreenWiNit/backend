package com.example.green.domain.member.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberExceptionMessage implements ExceptionMessage {

	MEMBER_NOT_FOUND(NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
	MEMBER_PROFILE_UPDATE_FAILED(INTERNAL_SERVER_ERROR, "프로필 업데이트에 실패했습니다."),
	MEMBER_NICKNAME_REQUIRED(BAD_REQUEST, "닉네임은 필수입니다."),
	MEMBER_NICKNAME_INVALID(BAD_REQUEST, "닉네임은 2자 이상 20자 이하여야 합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
