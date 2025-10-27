package com.example.green.domain.challenge.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamChallengeService {

	private final TeamChallengeRepository teamChallengeRepository;
	private final TeamChallengeQuery teamChallengeQuery;
	private final FileClient fileClient;
	private final SequenceService sequenceService;
	private final TimeUtils timeUtils;

	public Long create(AdminChallengeCreateDto dto) {
		String challengeCode = sequenceService.generateCode(SequenceType.TEAM_CHALLENGE, timeUtils.now());
		TeamChallenge challenge = dto.toTeamChallenge(challengeCode);
		TeamChallenge savedChallenge = teamChallengeRepository.save(challenge);
		fileClient.confirmUsingImage(savedChallenge.getChallengeImage());

		return savedChallenge.getId();
	}

	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public void join(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.addParticipation(memberId, timeUtils.now());
	}

	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public void leave(Long challengeId, Long memberId) {
		TeamChallenge teamChallenge = teamChallengeQuery.getTeamChallengeById(challengeId);
		teamChallenge.removeParticipation(memberId, timeUtils.now());
		// todo: 그룹 탈퇴 처리 필요, 하지만 지금 나가기 기능도 없어서 구현 X
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
			dto.challengeName(), dto.challengePoint(), dto.challengeContent()
		);

		String beforeImageUrl = teamChallenge.getChallengeImage();
		teamChallenge.updateImage(dto.challengeImageUrl());
		fileClient.swapImage(beforeImageUrl, teamChallenge.getChallengeImage());
	}
}
