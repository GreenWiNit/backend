package com.example.green.domain.example.api;

import com.example.green.global.api.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExamplePostResponseMessage implements ResponseMessage {
	POST_CREATED("게시글이 생성되었습니다."),
	POST_UPDATED("게시글이 수정되었습니다."),
	POST_FOUND("게시글이 조회되었습니다."),
	POST_DELETED("게시글이 삭제되었습니다.");

	private final String message;
}
