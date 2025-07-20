package com.example.green.domain.challengecert.controller.message;

import com.example.green.global.api.ResponseMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeCertificationResponseMessage implements ResponseMessage {
	CERTIFICATION_CREATED("챌린지 인증이 성공적으로 생성되었습니다.");

	private final String message;
} 