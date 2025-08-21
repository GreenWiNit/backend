package com.example.green.domain.challenge.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeExceptionMessage implements ExceptionMessage {

	// 챌린지
	CHALLENGE_NOT_FOUND("챌린지를 찾을 수 없습니다.", BAD_REQUEST),
	INACTIVE_CHALLENGE("현재 활성화된 챌린지가 아닙니다.", BAD_REQUEST),
	ALREADY_PARTICIPATING("이미 참여 중인 챌린지입니다. [홈]-[참여 챌린지]에서 확인해주세요.", BAD_REQUEST),
	NOT_PARTICIPATING_CHALLENGE("참여하지 않은 챌린지입니다.", BAD_REQUEST),
	INVALID_CHALLENGE_PERIOD("챌린지 시작일은 종료일보다 늦을 수 없습니다.", BAD_REQUEST),
	INVALID_MINIMUM_POINT("챌린지의 지급 포인트는 0원 이상이어야 합니다.", BAD_REQUEST),

	// 그룹 (client 에서는 team)
	INVALID_ACTIVE_PERIOD("해당 챌린지의 팀 활동 기간이 아닙니다.", BAD_REQUEST),
	INVALID_MAX_PARTICIPANTS_COUNT("팀 생성 시 최대 참여자 수는 0보다 커야 합니다.", BAD_REQUEST),
	MAX_PARTICIPANTS_LESS_THAN_CURRENT("최대 참여자 수는 현재 참여자 수보다 작을 수 없습니다.", BAD_REQUEST),
	GROUP_IS_FULL("챌린지 팀의 최대 참여자 수에 도달했습니다.", BAD_REQUEST),
	LEADER_USE_BE_DELETE("팀 리더는 팀 삭제를 이용해주세요.", FORBIDDEN),
	CHALLENGE_GROUP_NOT_FOUND("챌린지 팀 정보를 찾을 수 없습니다.", NOT_FOUND),
	INVALID_GROUP_MEMBERSHIP("팀에 접근 권한이 없습니다.", BAD_REQUEST),
	NOT_GROUP_LEADER("해당 팀의 리더가 아닙니다.", FORBIDDEN),
	ALREADY_PARTICIPATING_IN_GROUP("이미 해당 팀에 참여 중입니다.", BAD_REQUEST),
	INVALID_GROUP_PERIOD_RANGE("팀 활동 시작일은 종료일보다 늦을 수 없습니다.", BAD_REQUEST),
	INVALID_GROUP_ACTIVE_TIME("팀 활동 시작 시간을 현재보다 과거로 설정할 수 없습니다.", BAD_REQUEST),
	MISMATCH_GROUP_PERIOD_RANGE("팀 생성시 활동 기간은 챌린지 기간을 벗어날 수 없습니다.", BAD_REQUEST),
	ALREADY_PARTICIPATED_ON_THIS_DATE("해당 챌린지 활동 일자로 이미 참여한 팀이 존재합니다.", BAD_REQUEST),
	CANNOT_DELETE_AFTER_ACTIVITY_START("챌린지 활동 시작 이후에는 팀 정보를 삭제할 수 없습니다.", BAD_REQUEST);

	private final String message;
	private final HttpStatus httpStatus;
}
