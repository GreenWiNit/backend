package com.example.green.domain.challenge.controller.query.dto.group;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.example.green.domain.challenge.entity.group.GroupCapacity;
import com.example.green.domain.challenge.entity.group.GroupPeriod;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "팀 챌린지 그룹 목록 응답")
@Getter
@Setter
@NoArgsConstructor
public class MyChallengeGroupDto {

	@Schema(description = "그룹 ID", example = "1")
	private Long id;

	@Schema(description = "그룹명", example = "강남구 러닝 그룹")
	private String groupName;

	@Schema(description = "시군구 주소", example = "강남구")
	private String sigungu;

	@Schema(description = "챌린지 시작 일자")
	private LocalDate challengeDate;

	@Schema(description = "챌린지 시작 시간")
	private LocalTime startTime;

	@Schema(description = "챌린지 종료 시간")
	private LocalTime endTime;

	@Schema(description = "현재 참가자 수", example = "5")
	private Integer currentParticipants;

	@Schema(description = "최대 참가자 수", example = "10")
	private Integer maxParticipants;

	@Schema(description = "리더 여부", example = "true")
	private boolean leaderMe;

	@Schema(description = "인증 완료 여부", example = "true")
	private boolean certified;

	@Schema(description = "페이징 용, 무시 가능")
	private LocalDateTime createdDate;

	public MyChallengeGroupDto(
		Long id, String groupName, String sigungu, GroupPeriod period,
		GroupCapacity capacity, Boolean isLeader, LocalDateTime createdDate
	) {
		this.id = id;
		this.groupName = groupName;
		this.sigungu = sigungu;
		this.challengeDate = period.getDate();
		this.startTime = period.getStartTime();
		this.endTime = period.getEndTime();
		this.currentParticipants = capacity.getCurrentParticipants();
		this.maxParticipants = capacity.getMaxParticipants();
		this.leaderMe = isLeader;
		this.createdDate = createdDate;
	}

	public String getCursor() {
		return this.createdDate + "," + this.id;
	}
}