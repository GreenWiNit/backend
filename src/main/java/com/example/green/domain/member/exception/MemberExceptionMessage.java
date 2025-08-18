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
	MEMBER_NICKNAME_INVALID(BAD_REQUEST, "닉네임은 2자 이상 20자 이하여야 합니다."),
	MEMBER_NICKNAME_DUPLICATE(CONFLICT, "이미 사용 중인 닉네임입니다."),
	MEMBER_ALREADY_WITHDRAWN(BAD_REQUEST, "이미 탈퇴한 회원입니다."),
	MEMBER_WITHDRAW_FAILED(INTERNAL_SERVER_ERROR, "회원 탈퇴 처리에 실패했습니다."),
	WITHDRAW_CUSTOM_REASON_REQUIRED(BAD_REQUEST, "기타 사유 선택 시 상세 사유를 입력해주세요."),
	DUPLICATE_WITHDRAW_REASONS(BAD_REQUEST, "중복된 탈퇴 사유를 선택할 수 없습니다.")
	;

	private final HttpStatus httpStatus;
	private final String message;
}
