package com.example.green.domain.challenge.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeExceptionMessage implements ExceptionMessage {

	CHALLENGE_NOT_FOUND("챌린지를 찾을 수 없습니다.", 404),
	CHALLENGE_NOT_PARTICIPATABLE("챌린지 참여 기간이 아닙니다.", 400),
	CHALLENGE_NOT_LEAVEABLE("챌린지 탈퇴 기간이 아닙니다.", 400),
	ALREADY_PARTICIPATING("이미 참여 중인 챌린지입니다.", 400),
	NOT_PARTICIPATING("참여하지 않은 챌린지입니다.", 400),
	INVALID_MAX_GROUP_COUNT("최대 그룹 수는 0보다 커야 합니다.", 400),
	INVALID_MAX_PARTICIPANTS_COUNT("최대 참여자 수는 0보다 커야 합니다.", 400),
	NO_AVAILABLE_MEMBER_FOR_LEADER("리더로 지정할 수 있는 멤버가 없습니다.", 400),
	GROUP_IS_FULL("그룹의 최대 참여자 수에 도달했습니다.", 400),
	LEADER_CANNOT_LEAVE_WITH_MEMBERS("멤버가 있는 상태에서 리더는 탈퇴할 수 없습니다.", 400),
	CANNOT_LEAVE_WHILE_IN_GROUP("그룹에 참여 중인 상태에서는 팀 챌린지에서 탈퇴할 수 없습니다.", 400);

	private final String message;
	private final int status;

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.valueOf(status);
	}
}
