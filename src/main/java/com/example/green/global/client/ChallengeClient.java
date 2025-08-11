package com.example.green.global.client;

import com.example.green.global.client.dto.ChallengeDto;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId, Long groupId, Long memberId);

	ChallengeDto getPersonalChallenge(Long challengeId);
}
