package com.example.green.domain.certification.ui;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CertificationResponseMessage implements ResponseMessage {

	TEAM_CHALLENGE_CERTIFICATE_SUCCESS("팀 챌린지 인증에 성공했습니다."),
	;

	private final String message;
}
