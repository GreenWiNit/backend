package com.example.green.domain.challenge.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.global.utils.TimeUtils;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalChallengeService {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final FileClient fileClient;
	private final SequenceService sequenceService;
	private final TimeUtils timeUtils;

	public Long create(AdminChallengeCreateDto request) {
		String challengeCode = sequenceService.generateCode(SequenceType.PERSONAL_CHALLENGE, timeUtils.now());
		PersonalChallenge challenge = request.toPersonalChallenge(challengeCode);
		PersonalChallenge savedChallenge = personalChallengeRepository.save(challenge);
		fileClient.confirmUsingImage(savedChallenge.getChallengeImage());

		return savedChallenge.getId();
	}

	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public void join(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.addParticipation(memberId, timeUtils.now());
	}

	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public void leave(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.removeParticipation(memberId, timeUtils.now());
	}

	public void show(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.show();
	}

	public void hide(Long challengeId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.hide();
	}

	public void update(Long challengeId, AdminChallengeUpdateDto dto) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.updateBasicInfo(
			dto.challengeName(), dto.challengePoint(), dto.beginDate(), dto.endDate(), dto.challengeContent()
		);

		String beforeImageUrl = personalChallenge.getChallengeImage();
		personalChallenge.updateImage(dto.challengeImageUrl());
		fileClient.swapImage(beforeImageUrl, personalChallenge.getChallengeImage());
	}
}
