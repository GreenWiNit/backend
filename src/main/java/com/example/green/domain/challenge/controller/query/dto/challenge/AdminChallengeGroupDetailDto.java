package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "어드민 팀 챌린지 그룹 상세 응답")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminChallengeGroupDetailDto {

	@Schema(description = "그룹(팀) ID", example = "1")
	private Long groupId;

	@Schema(description = "그룹(팀) 코드", example = "T-20250109-001")
	private String groupCode;

	@Schema(description = "그룹(팀) 리더 MemberKey", example = "google_4534")
	private String leaderMemberKey;

	@Schema(description = "그룹(팀)원 MemberKey", example = "google_3927, naver_9174")
	private List<String> participantMemberKeys;

	@Schema(description = "그룹(팀) 제목", example = "함께 플롯길 해요~")
	private String groupName;

	@Schema(description = "날짜", example = "2025-06-08")
	private LocalDate date;

	@Schema(description = "시작 시간", example = "20:00")
	private LocalTime startTime;

	@Schema(description = "종료 시간", example = "21:00")
	private LocalTime endTime;

	@Schema(description = "장소", example = "서울시 종로구 00강 입구")
	private String fullAddress;

	@Schema(description = "설명", example = "1시간 동안 함께 플롯길 하는 코스입니다.")
	private String description;

	@Schema(description = "오픈채팅방 링크", example = "https://open.kakao.com/o/sAczYWth")
	private String openChatUrl;

	@JsonIgnore
	private Long leaderId;
	@JsonIgnore
	private List<Long> participantIds;

	public static AdminChallengeGroupDetailDto from(ChallengeGroup challengeGroup) {
		return AdminChallengeGroupDetailDto.builder()
			.groupId(challengeGroup.getId())
			.groupCode(challengeGroup.getTeamCode())
			.groupName(challengeGroup.getBasicInfo().getGroupName())
			.date(challengeGroup.getPeriod().getDate())
			.startTime(challengeGroup.getPeriod().getStartTime())
			.endTime(challengeGroup.getPeriod().getEndTime())
			.fullAddress(challengeGroup.getGroupAddress().getFullAddress())
			.description(challengeGroup.getBasicInfo().getDescription())
			.openChatUrl(challengeGroup.getBasicInfo().getOpenChatUrl())
			.leaderId(challengeGroup.getLeaderId())
			.participantIds(challengeGroup.getParticipantIds())
			.build();
	}
}
