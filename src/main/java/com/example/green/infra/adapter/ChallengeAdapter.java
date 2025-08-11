package com.example.green.infra.adapter;

import org.springframework.stereotype.Component;

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
	public ChallengeDto getTeamChallenge(Long challengeId, Long groupId, Long memberId) {
		TeamChallenge challenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		ChallengeGroup group = challengeGroupQuery.getChallengeGroup(groupId, memberId);
		return ChallengeDto.ofTeam(challenge, group.getTeamCode());
	}

	@Override
	public ChallengeDto getPersonalChallenge(Long challengeId) {
		PersonalChallenge challenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		return ChallengeDto.ofPersonal(challenge);
	}
}
