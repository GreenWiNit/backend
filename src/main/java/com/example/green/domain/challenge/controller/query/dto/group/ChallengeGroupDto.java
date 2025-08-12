package com.example.green.domain.challenge.controller.query.dto.group;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.green.domain.challenge.entity.group.GroupCapacity;
import com.example.green.domain.challenge.entity.group.GroupPeriod;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팀 챌린지 그룹 목록 응답")
public record ChallengeGroupDto(
	@Schema(description = "그룹 ID", example = "1")
	Long id,
	@Schema(description = "그룹명", example = "강남구 러닝 그룹")
	String groupName,
	@Schema(description = "시군구 주소", example = "강남구")
	String sigungu,
	@Schema(description = "챌린지 시작 일자")
	LocalDate challengeDate,
	@Schema(description = "챌린지 시작 시간")
	LocalTime startTime,
	@Schema(description = "챌린지 종료 시간")
	LocalTime endTime,
	@Schema(description = "현재 참가자 수", example = "5")
	Integer currentParticipants,
	@Schema(description = "최대 참가자 수", example = "10")
	Integer maxParticipants,
	@Schema(description = "페이징 용, 무시 가능")
	LocalDateTime createdDate
) {

	public ChallengeGroupDto(
		Long id, String groupName, String sigungu, GroupPeriod period,
		GroupCapacity capacity, LocalDateTime createdDate
	) {
		this(
			id, groupName, sigungu, period.getDate(), period.getStartTime(), period.getEndTime(),
			capacity.getCurrentParticipants(), capacity.getMaxParticipants(), createdDate
		);
	}
}
