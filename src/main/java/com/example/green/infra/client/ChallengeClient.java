package com.example.green.infra.client;

import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId);

	ChallengeDto getPersonalChallengeByMember(Long challengeId, Long memberId);

	ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId);

	void confirmTeamCertification(CertificationConfirmRequest request);
}
