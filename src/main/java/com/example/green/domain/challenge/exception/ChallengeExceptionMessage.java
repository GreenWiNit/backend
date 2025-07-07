package com.example.green.domain.challenge.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ChallengeExceptionMessage implements ExceptionMessage {

	// 챌린지 상태 변경 관련
	INVALID_CHALLENGE_START(BAD_REQUEST, "챌린지를 시작할 수 없는 상태입니다."),
	INVALID_CHALLENGE_COMPLETE(BAD_REQUEST, "챌린지를 완료할 수 없는 상태입니다."),
	INVALID_CHALLENGE_DEADLINE(BAD_REQUEST, "챌린지를 마감할 수 없는 상태입니다."),
	CHALLENGE_NOT_STARTED_YET(BAD_REQUEST, "아직 시작 시간이 되지 않았습니다."),
	CHALLENGE_ALREADY_STARTED(BAD_REQUEST, "이미 시작된 챌린지입니다."),
	CHALLENGE_ALREADY_COMPLETED(BAD_REQUEST, "이미 완료된 챌린지입니다."),

	// 챌린지 참여 관련
	CHALLENGE_NOT_PROCEEDING(BAD_REQUEST, "진행중인 챌린지가 아닙니다."),
	CHALLENGE_EXPIRED(BAD_REQUEST, "만료된 챌린지입니다."),

	// 팀 챌린지 관련
	TEAM_CHALLENGE_FULL(BAD_REQUEST, "팀 챌린지 참여 인원이 가득 찼습니다."),
	INVALID_TEAM_COUNT(BAD_REQUEST, "팀 수가 올바르지 않습니다."),
	INVALID_MAX_GROUP_COUNT(BAD_REQUEST, "최대 그룹 수는 1 이상이어야 합니다."),
	INVALID_MAX_PARTICIPANTS_COUNT(BAD_REQUEST, "최대 참가자 수는 1 이상이어야 합니다."),

	// 일반 챌린지 관련
	CHALLENGE_NOT_FOUND(NOT_FOUND, "해당 챌린지를 찾을 수 없습니다."),
	INVALID_CHALLENGE_DATA(BAD_REQUEST, "챌린지 데이터가 올바르지 않습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
