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
	DETAIL_FOUND("챌린지 인증 상세 정보가 성공적으로 조회되었습니다."),
	
	// 관리자용 메시지들
	ADMIN_PERSONAL_CHALLENGE_TITLES_FOUND("개인 챌린지 제목 목록이 성공적으로 조회되었습니다."),
	ADMIN_TEAM_CHALLENGE_TITLES_FOUND("팀 챌린지 제목 목록이 성공적으로 조회되었습니다."),
	ADMIN_PARTICIPANT_MEMBER_KEYS_FOUND("참여자 memberKey 목록이 성공적으로 조회되었습니다."),
	ADMIN_GROUP_CODES_FOUND("팀 챌린지 그룹 코드 목록이 성공적으로 조회되었습니다."),
	ADMIN_CERTIFICATION_STATUS_UPDATED("인증 상태가 성공적으로 업데이트되었습니다."),
	ADMIN_PERSONAL_CERTIFICATIONS_WITH_FILTERS_FOUND("필터 조건에 따른 개인 챌린지 인증 목록이 성공적으로 조회되었습니다."),
	ADMIN_TEAM_CERTIFICATIONS_WITH_FILTERS_FOUND("필터 조건에 따른 팀 챌린지 인증 목록이 성공적으로 조회되었습니다.");

	private final String message;
}
