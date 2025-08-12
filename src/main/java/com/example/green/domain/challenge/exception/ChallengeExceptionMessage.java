package com.example.green.domain.challenge.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeExceptionMessage implements ExceptionMessage {

	CHALLENGE_NOT_FOUND("챌린지를 찾을 수 없습니다.", 404),
	INACTIVE_CHALLENGE("현재 활성화된 챌린지가 아닙니다.", 400),
	INACTIVE_GROUP("현재 활성화된 그룹이 아닙니다.", 400),
	ALREADY_PARTICIPATING("이미 참여 중인 챌린지입니다.", 400),
	NOT_PARTICIPATING("참여하지 않은 챌린지입니다.", 400),
	INVALID_MAX_GROUP_COUNT("최대 그룹 수는 0보다 커야 합니다.", 400),
	INVALID_MAX_PARTICIPANTS_COUNT("최대 참여자 수는 0보다 커야 합니다.", 400),
	MAX_PARTICIPANTS_LESS_THAN_CURRENT("최대 참여자 수는 현재 참여자 수보다 작을 수 없습니다.", 400),
	NO_AVAILABLE_MEMBER_FOR_LEADER("리더로 지정할 수 있는 멤버가 없습니다.", 400),
	GROUP_IS_FULL("그룹의 최대 참여자 수에 도달했습니다.", 400),
	LEADER_USE_BE_DELETE("리더는 나가기를 이용해주세요.", 400),
	CANNOT_LEAVE_WHILE_IN_GROUP("그룹에 참여 중인 상태에서는 팀 챌린지에서 탈퇴할 수 없습니다.", 400),
	CHALLENGE_GROUP_NOT_FOUND("챌린지 그룹을 찾을 수 없습니다.", 404),
	MEMBER_NOT_FOUND("회원을 찾을 수 없습니다.", 404),
	INVALID_GROUP_MEMBERSHIP("해당 팀 챌린지 그룹의 권한이 없습니다.", 403),
	ALREADY_PARTICIPATING_IN_GROUP("이미 해당 팀(그룹)에 참여 중입니다.", 400),
	CANNOT_PARTICIPATE_IN_GROUP("그룹에 참여할 수 없습니다.", 400),
	NOT_GROUP_LEADER("그룹 리더가 아닙니다.", 403),
	INVALID_CHALLENGE_PERIOD("챌린지 시작일시는 종료일시보다 이전이어야 합니다.", 400),

	// Admin 전용 예외 메시지
	ADMIN_CHALLENGE_NOT_FOUND("관리자 - 챌린지를 찾을 수 없습니다.", 404),
	ADMIN_TEAM_CHALLENGE_GROUP_NOT_FOUND("관리자 - 팀 챌린지 그룹을 찾을 수 없습니다.", 404),
	ADMIN_INVALID_CHALLENGE_TYPE("관리자 - 지원하지 않는 챌린지 유형입니다.", 400),
	ADMIN_CHALLENGE_CREATE_FAILED("관리자 - 챌린지 생성에 실패했습니다.", 500),
	ADMIN_CHALLENGE_UPDATE_FAILED("관리자 - 챌린지 수정에 실패했습니다.", 500),
	INVALID_MINIMUM_POINT("포인트는 0원 이상입니다.", 400),
	INVALID_GROUP_PERIOD("팀 활동 시작일시는 종료일시보다 이전이어야 합니다.", 400),
	MISMATCH_GROUP_PERIOD_RANGE("그룹 활동이 팀 챌린지 기간 내에 포함되지 않습니다.", 400);

	private final String message;
	private final int status;

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.valueOf(status);
	}
}
