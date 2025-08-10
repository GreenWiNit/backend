package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challenge.utils.CodeGenerator;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final TimeUtils timeUtils;

	public Long createPersonalChallenge(AdminChallengeCreateRequestDto request) {
		long count = personalChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generatePersonalCode(timeUtils.now(), count + 1);
		PersonalChallenge challenge = PersonalChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		PersonalChallenge savedChallenge = personalChallengeRepository.save(challenge);
		return savedChallenge.getId();
	}

	public Long createTeamChallenge(AdminChallengeCreateRequestDto request) {
		long count = teamChallengeRepository.countChallengesByCreatedDate(timeUtils.now());
		String challengeCode = CodeGenerator.generateTeamCode(timeUtils.now(), count + 1);
		TeamChallenge challenge = TeamChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		TeamChallenge saved = teamChallengeRepository.save(challenge);
		return saved.getId();
	}

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

	public void showTeamChallenge(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.show();
	}

	public void hideTeamChallenge(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.hide();
	}

	public void showPersonalChallenge(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.show();
	}

	public void hidePersonalChallenge(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.hide();
	}
}
