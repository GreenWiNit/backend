package com.example.green.infra.client.dto;

import java.time.LocalDate;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public record ChallengeGroupDto(
	Long id,
	Long challengeId,
	String groupCode,
	LocalDate challengeDate
) {

	public static ChallengeGroupDto from(ChallengeGroup challengeGroup) {
		return new ChallengeGroupDto(
			challengeGroup.getId(),
			challengeGroup.getTeamChallengeId(),
			challengeGroup.getTeamCode(),
			challengeGroup.getPeriod().getDate()
		);
	}
}
