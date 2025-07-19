package com.example.green.domain.challenge.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamChallengeGroupResponseMessage implements ResponseMessage {

	GROUP_LIST_FOUND("그룹 목록을 조회했습니다."),
	GROUP_DETAIL_FOUND("그룹 상세 정보를 조회했습니다."),
	GROUP_CREATED("그룹을 생성했습니다."),
	GROUP_JOINED("그룹에 참가했습니다."),
	GROUP_UPDATED("그룹 정보를 수정했습니다."),
	GROUP_DELETED("그룹을 삭제했습니다.");

	private final String message;
}
