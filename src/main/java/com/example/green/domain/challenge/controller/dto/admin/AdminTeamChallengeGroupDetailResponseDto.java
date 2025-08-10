package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.green.domain.challenge.entity.group.TeamChallengeGroup;
import com.example.green.domain.challenge.entity.group.TeamChallengeGroupParticipation;
import com.example.green.domain.challenge.entity.group.GroupRoleType;

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

	/**
	 * TeamChallengeGroup 엔티티와 참여자 목록으로부터 AdminTeamChallengeGroupDetailResponseDto를 생성합니다.
	 */
	// todo: challenge
	public static AdminTeamChallengeGroupDetailResponseDto from(TeamChallengeGroup group,
		List<TeamChallengeGroupParticipation> participants) {
		// 팀장 찾기
		String leaderMemberKey = participants.stream()
			.filter(p -> p.getGroupRoleType() == GroupRoleType.LEADER)
			.findFirst()
			//.map(p -> p.getTeamChallengeParticipation().getMemberId().getMemberKey())
			.map(it -> "TODO: 기능 구현")
			.orElse("");

		// 참여자 MemberKey 목록 생성
		String participantMemberKeys = participants.stream()
			.filter(p -> p.getGroupRoleType() == GroupRoleType.MEMBER)
			//.map(p -> p.getTeamChallengeParticipation().getMemberId().getMemberKey())
			.map(it -> "TODO: 기능 구현")
			.reduce((a, b) -> a + ", " + b)
			.orElse("");

		return new AdminTeamChallengeGroupDetailResponseDto(
			group.getTeamCode(),
			leaderMemberKey,
			participantMemberKeys,
			group.getGroupName(),
			group.getGroupBeginDateTime().toLocalDate(),
			group.getGroupBeginDateTime().toLocalTime(),
			group.getGroupEndDateTime().toLocalTime(),
			group.getGroupAddress() != null ? group.getGroupAddress().getFullAddress() : "",
			group.getGroupDescription(),
			group.getOpenChatUrl()
		);
	}
}
