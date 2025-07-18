package com.example.green.domain.challenge.controller.dto;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.GroupStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팀 챌린지 그룹 상세 응답")
public record TeamChallengeGroupDetailResponseDto(
	@Schema(description = "그룹 ID", example = "1")
	Long id,

	@Schema(description = "그룹명", example = "강남구 러닝 그룹")
	String groupName,

	@Schema(description = "그룹 주소", example = "서울시 강남구 테헤란로 123")
	String groupAddress,

	@Schema(description = "그룹 설명", example = "매주 화, 목 저녁 7시에 모여서 5km 러닝합니다.")
	String groupDescription,

	@Schema(description = "오픈 채팅 URL", example = "https://open.kakao.com/o/abc123")
	String openChatUrl,

	@Schema(description = "그룹 시작 일시")
	LocalDateTime groupBeginDateTime,

	@Schema(description = "그룹 종료 일시")
	LocalDateTime groupEndDateTime,

	@Schema(description = "현재 참가자 수", example = "5")
	Integer currentParticipants,

	@Schema(description = "최대 참가자 수", example = "10")
	Integer maxParticipants,

	@Schema(description = "그룹 상태")
	GroupStatus groupStatus,

	@Schema(description = "리더 여부", example = "true")
	Boolean isLeader,

	@Schema(description = "참가 여부", example = "true")
	Boolean isParticipant
) {
}
