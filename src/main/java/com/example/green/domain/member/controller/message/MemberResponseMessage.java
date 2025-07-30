package com.example.green.domain.member.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberResponseMessage implements ResponseMessage {

	NICKNAME_AVAILABLE("사용 가능한 닉네임입니다."),
	NICKNAME_TAKEN("중복된 닉네임이 존재합니다."),

	PROFILE_UPDATED("프로필이 성공적으로 수정되었습니다."),
	PROFILE_FOUND("프로필 조회에 성공했습니다."),

	MEMBER_LIST_RETRIEVED("회원 목록 조회가 완료되었습니다."),
	WITHDRAWN_MEMBER_LIST_RETRIEVED("탈퇴 회원 목록 조회가 완료되었습니다."),
	MEMBER_DELETED("회원 삭제가 완료되었습니다.")
	;

	private final String message;
} 