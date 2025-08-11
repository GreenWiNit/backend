package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.domain.common.service.FileManager;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamChallengeService {

	private final TeamChallengeRepository teamChallengeRepository;
	private final TeamChallengeQuery teamChallengeQuery;
	private final FileManager fileManager;
	private final SequenceService sequenceService;
	private final TimeUtils timeUtils;

	public Long create(AdminChallengeCreateDto request) {
		String challengeCode = sequenceService.generateCode(SequenceType.TEAM_CHALLENGE, timeUtils.now());
		TeamChallenge challenge = TeamChallenge.create(
			challengeCode, request.challengeName(), request.challengeImageUrl(), request.challengeContent(),
			request.challengePoint(), request.beginDateTime(), request.endDateTime()
		);

		TeamChallenge savedChallenge = teamChallengeRepository.save(challenge);
		fileManager.confirmUsingImage(savedChallenge.getChallengeImage());

		return savedChallenge.getId();
	}

	public void join(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.addParticipation(memberId, timeUtils.now());
	}

	public void leave(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.removeParticipation(memberId, timeUtils.now());
		// todo: 그룹 탈퇴 처리 필요
	}

	public void show(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.show();
	}

	public void hide(Long challengeId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.hide();
	}

	public void update(Long challengeId, AdminChallengeUpdateDto dto) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.updateBasicInfo(
			dto.challengeName(), dto.challengePoint(), dto.beginDateTime(), dto.endDateTime(), dto.challengeContent()
		);

		String beforeImageUrl = teamChallenge.getChallengeImage();
		teamChallenge.updateImage(dto.challengeImageUrl());
		fileManager.swapImage(beforeImageUrl, teamChallenge.getChallengeImage());
	}
}
