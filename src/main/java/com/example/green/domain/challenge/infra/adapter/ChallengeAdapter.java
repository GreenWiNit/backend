package com.example.green.domain.challenge.infra.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challenge.service.ChallengeGroupService;
import com.example.green.infra.client.ChallengeClient;
import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeAdapter implements ChallengeClient {

	private final ChallengeGroupService challengeGroupService;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final ChallengeGroupQuery challengeGroupQuery;

	@Override
	public ChallengeDto getTeamChallenge(Long challengeId) {
		
		TeamChallenge challenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeDto getPersonalChallengeByMemberAndDate(Long challengeId, Long memberId, LocalDate challengeDate) {
		PersonalChallenge challenge =
			personalChallengeQuery.getPersonalChallengeByMemberAndDate(challengeId, memberId, challengeDate);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId) {
		challengeGroupQuery.validateMembership(groupId, memberId);
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId);
		return ChallengeGroupDto.from(challengeGroup);
	}

	@Override
	public void confirmTeamCertification(CertificationConfirmRequest request) {
		challengeGroupService.confirmTeamCertification(request.groupId(), request.memberId());
	}
}
