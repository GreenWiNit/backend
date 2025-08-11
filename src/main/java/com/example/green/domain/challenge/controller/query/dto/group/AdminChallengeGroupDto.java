package com.example.green.domain.challenge.controller.query.dto.group;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.group.GroupStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 그룹 목록 응답")
public record AdminChallengeGroupDto(
	@Schema(description = "그룹 ID", example = "1")
	Long id,

	@Schema(description = "그룹 코드", example = "T-20250809-001")
	String groupCode,

	@Schema(description = "그룹명", example = "함께 플로길 해요~")
	String groupName,

	@Schema(description = "등록 날짜", example = "2025-08-09")
	LocalDateTime registrationDate,

	@Schema(description = "최대 인원", example = "15")
	Integer maxParticipants,

	@Schema(description = "현재 참여 인원", example = "14")
	Integer currentParticipants,

	@Schema(description = "모집 여부")
	GroupStatus recruitmentStatus
) {

	public LocalDate getRegistrationDate() {
		return registrationDate.toLocalDate();
	}
}
