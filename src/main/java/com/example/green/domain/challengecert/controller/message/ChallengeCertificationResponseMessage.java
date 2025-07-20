package com.example.green.domain.challengecert.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeCertificationResponseMessage implements ResponseMessage {
	CERTIFICATION_CREATED("챌린지 인증이 성공적으로 생성되었습니다."),
	PERSONAL_LIST_FOUND("개인 챌린지 인증 목록이 성공적으로 조회되었습니다."),
	TEAM_LIST_FOUND("팀 챌린지 인증 목록이 성공적으로 조회되었습니다."),
	DETAIL_FOUND("챌린지 인증 상세 정보가 성공적으로 조회되었습니다.");

	private final String message;
}
