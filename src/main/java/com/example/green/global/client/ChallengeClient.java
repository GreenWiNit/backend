package com.example.green.global.client;

import com.example.green.domain.certification.ui.dto.ChallengeGroupDto;
import com.example.green.global.client.dto.ChallengeDto;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId);

	ChallengeDto getPersonalChallenge(Long challengeId);

	ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId);
}
