package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final TimeUtils timeUtils;

	public void joinPersonalChallenge(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.addParticipation(memberId, timeUtils.now());
	}

	public void joinTeamChallenge(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.addParticipation(memberId, timeUtils.now());
	}

	public void leavePersonalChallenge(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.removeParticipation(memberId, timeUtils.now());
	}

	public void leaveTeamChallenge(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.removeParticipation(memberId, timeUtils.now());
		// todo: 그룹 탈퇴 처리 필요
	}
}
