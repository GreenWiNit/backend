package com.example.green.domain.certification.ui;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CertificationResponseMessage implements ResponseMessage {

	TEAM_CHALLENGE_CERTIFICATE_SUCCESS("팀 챌린지 인증에 성공했습니다."),
	PERSONAL_CHALLENGE_CERTIFICATE_SUCCESS("개인 챌린지 인증에 성공했습니다."),
	CERTIFICATIONS_READ_SUCCESS("챌린지 인증 목록 조회에 성공헀습니다."),
	CERTIFICATION_DETAIL_READ_SUCCESS("챌린지 인증 상세 정보 조회에 성공헀습니다."),
	CERTIFICATIONS_APPROVE_SUCCESS("인증된 챌린지 목록 포인트 지급 처리가 성공했습니다."),
	CERTIFICATIONS_REJECT_SUCCESS("인증된 챌린지 목록 포인트 미지급 처리가 성공했습니다.");;

	private final String message;
}
