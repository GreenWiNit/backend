package com.example.green.infra.client;

import java.time.LocalDate;
import java.util.List;

import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;

public interface ChallengeClient {

	ChallengeDto getTeamChallenge(Long challengeId);

	ChallengeDto getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate);

	ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId);

	void confirmTeamCertifications(List<CertificationConfirmRequest> teamConfirmRequests);
}
