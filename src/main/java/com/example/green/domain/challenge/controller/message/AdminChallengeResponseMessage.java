package com.example.green.domain.challenge.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminChallengeResponseMessage implements ResponseMessage {
	// 챌린지 관리
	CHALLENGE_CREATED("챌린지가 성공적으로 생성되었습니다."),
	CHALLENGE_UPDATED("챌린지가 성공적으로 수정되었습니다."),
	CHALLENGE_DELETED("챌린지가 성공적으로 삭제되었습니다."),
	CHALLENGE_SHOW("챌린지가 전시 상태로 변경되었습니다."),
	CHALLENGE_HIDE("챌린지가 미전시 상태로 변경되었습니다."),
	CHALLENGE_IMAGE_UPDATED("챌린지 이미지가 성공적으로 변경되었습니다."),

	// 챌린지 조회
	PERSONAL_CHALLENGE_LIST_FOUND("개인 챌린지 목록 조회에 성공했습니다."),
	TEAM_CHALLENGE_LIST_FOUND("팀 챌린지 목록 조회에 성공했습니다."),
	CHALLENGE_DETAIL_FOUND("챌린지 상세 조회에 성공했습니다."),
	CHALLENGE_PARTICIPANTS_FOUND("챌린지 참여자 목록 조회에 성공했습니다."),

	// 그룹 관리
	GROUP_LIST_FOUND("그룹 목록 조회에 성공했습니다."),
	GROUP_DETAIL_FOUND("그룹 상세 조회에 성공했습니다."),
	GROUP_DELETED("그룹이 성공적으로 삭제되었습니다."),

	// 통계
	CHALLENGE_STATISTICS_FOUND("챌린지 통계 조회에 성공했습니다."),
	;

	private final String message;
}
