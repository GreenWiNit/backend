package com.example.green.infra.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public record ChallengeGroupDto(
	Long id,
	Long challengeId,
	String groupCode,
	LocalDateTime afterDateTime
) {

	public static ChallengeGroupDto from(ChallengeGroup challengeGroup) {
		return new ChallengeGroupDto(
			challengeGroup.getId(),
			challengeGroup.getTeamChallengeId(),
			challengeGroup.getTeamCode(),
			challengeGroup.getPeriod().getEndDateTime()
		);
	}

	public LocalDate challengeDate() {
		return afterDateTime.toLocalDate();
	}
}
