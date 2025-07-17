package com.example.green.domain.challenge.controller.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeParticipationStatus {
	NOT_LOGGED_IN("비로그인"),
	NOT_JOINED("참여하지 않음"),
	JOINED("참여됨");

	private final String description;
}
