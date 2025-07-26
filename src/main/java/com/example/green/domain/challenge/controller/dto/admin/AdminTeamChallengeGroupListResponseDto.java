package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDate;

import com.example.green.domain.challenge.entity.TeamChallengeGroup;
import com.example.green.domain.challenge.enums.GroupStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 그룹 목록 응답")
public record AdminTeamChallengeGroupListResponseDto(
	@Schema(description = "그룹 ID", example = "1")
	Long id,

	@Schema(description = "팀코드", example = "T-20250109-143523-C8NQ")
	String teamCode,

	@Schema(description = "그룹명", example = "함께 플로길 해요~")
	String teamTitle,

	@Schema(description = "등록 날짜", example = "2025-08-09")
	LocalDate registrationDate,

	@Schema(description = "최대 인원", example = "15")
	Integer maxParticipants,

	@Schema(description = "현재 참여 인원", example = "14")
	Integer currentParticipants,

	@Schema(description = "모집 여부")
	GroupStatus recruitmentStatus
) {

	/**
	 * TeamChallengeGroup 엔티티로부터 AdminTeamChallengeGroupListResponseDto를 생성합니다.
	 */
	public static AdminTeamChallengeGroupListResponseDto from(TeamChallengeGroup group) {
		return new AdminTeamChallengeGroupListResponseDto(
			group.getId(),
			group.getTeamCode(),
			group.getGroupName(),
			group.getCreatedDate().toLocalDate(),
			group.getMaxParticipants(),
			group.getCurrentParticipants(),
			group.getGroupStatus()
		);
	}
}
