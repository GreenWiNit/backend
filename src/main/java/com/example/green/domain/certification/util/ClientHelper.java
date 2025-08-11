package com.example.green.domain.certification.util;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.global.client.ChallengeClient;
import com.example.green.global.client.dto.ChallengeDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ClientHelper {

	private final ChallengeClient challengeClient;

	public ChallengeSnapshot getPersonalSnapshot(Long challengeId) {
		ChallengeDto dto = challengeClient.getPersonalChallenge(challengeId);
		return ChallengeSnapshot.ofPersonal(dto.id(), dto.name(), dto.code());
	}

	public ChallengeSnapshot getTeamSnapshot(Long challengeId, Long groupId, Long memberId) {
		ChallengeDto dto = challengeClient.getTeamChallenge(challengeId, groupId, memberId);
		return ChallengeSnapshot.ofTeam(dto.id(), dto.name(), dto.code(), dto.groupCode());
	}
}
