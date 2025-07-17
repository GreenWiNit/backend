package com.example.green.domain.challenge.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeResponseMessage implements ResponseMessage {
	CHALLENGE_LIST_FOUND("챌린지 목록 조회에 성공했습니다."),
	CHALLENGE_DETAIL_FOUND("챌린지 상세 조회에 성공했습니다."),
	MY_PERSONAL_CHALLENGE_LIST_FOUND("내 개인 챌린지 목록 조회에 성공했습니다."),
	MY_TEAM_CHALLENGE_LIST_FOUND("내 팀 챌린지 목록 조회에 성공했습니다."),
	CHALLENGE_JOINED("챌린지 참여에 성공했습니다."),
	CHALLENGE_LEFT("챌린지 탈퇴에 성공했습니다.");

	private final String message;
}
