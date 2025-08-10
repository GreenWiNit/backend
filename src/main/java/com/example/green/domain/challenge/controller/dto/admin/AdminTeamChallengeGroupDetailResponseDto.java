package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 그룹 상세 응답")
public record AdminTeamChallengeGroupDetailResponseDto(
	@Schema(description = "팀 코드", example = "T-20250109-143523-C8NQ")
	String teamCode,

	@Schema(description = "팀 등록자 MemberKey(팀장)", example = "google_4534")
	String leaderMemberKey,

	@Schema(description = "참가 팀원 MemberKey", example = "google_3927, naver_9174")
	String participantMemberKeys,

	@Schema(description = "팀 제목", example = "함께 플롯길 해요~")
	String teamTitle,

	@Schema(description = "날짜", example = "2025-06-08")
	LocalDate date,

	@Schema(description = "시작 시간", example = "20:00")
	LocalTime startTime,

	@Schema(description = "종료 시간", example = "21:00")
	LocalTime endTime,

	@Schema(description = "장소", example = "서울시 종로구 00강 입구")
	String location,

	@Schema(description = "설명", example = "1시간 동안 함께 플롯길 하는 코스입니다.")
	String description,

	@Schema(description = "오픈채팅방 링크", example = "https://open.kakao.com/o/sAczYWth")
	String openChatRoomLink
) {
}
