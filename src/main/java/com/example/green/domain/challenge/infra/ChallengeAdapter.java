package com.example.green.domain.challenge.infra;

import org.springframework.stereotype.Component;

import com.example.green.domain.certification.ui.dto.ChallengeGroupDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.client.ChallengeClient;
import com.example.green.global.client.dto.ChallengeDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChallengeAdapter implements ChallengeClient {

	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final ChallengeGroupQuery challengeGroupQuery;

	@Override
	public ChallengeDto getTeamChallenge(Long challengeId) {
		TeamChallenge challenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeDto getPersonalChallenge(Long challengeId) {
		PersonalChallenge challenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId) {
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId, memberId);
		return ChallengeGroupDto.from(challengeGroup);
	}
}
