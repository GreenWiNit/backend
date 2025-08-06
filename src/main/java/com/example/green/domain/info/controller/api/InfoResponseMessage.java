package com.example.green.domain.info.controller.api;

import com.example.green.global.api.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InfoResponseMessage implements ResponseMessage {
	INFO_CREATED("정보공유 글이 생성되었습니다."),
	INFO_UPDATED("정보공유 글이 수정되었습니다."),
	INFO_DETAIL_FOUND("정보공유 글이 조회되었습니다."),
	INFO_LIST_FOUND("정보공유 목록이 조회되었습니다."),
	INFO_DELETED("정보공유 글이 삭제되었습니다."),
	GET_INFOCATEGORIES_SUCCESS("카테고리 목록 조회에 성공했습니다.");
	private final String Message;
}
