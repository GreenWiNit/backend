package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.domain.common.service.FileManager;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonalChallengeService {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final FileManager fileManager;
	private final SequenceService sequenceService;
	private final TimeUtils timeUtils;

	public Long create(AdminChallengeCreateDto request) {
		String challengeCode = sequenceService.generateCode(SequenceType.PERSONAL_CHALLENGE, timeUtils.now());
		PersonalChallenge challenge = request.toPersonalChallenge(challengeCode);
		PersonalChallenge savedChallenge = personalChallengeRepository.save(challenge);
		fileManager.confirmUsingImage(savedChallenge.getChallengeImage());

		return savedChallenge.getId();
	}

	public void join(Long challengeId, Long memberId) {
		PersonalChallenge personalChallenge = personalChallengeQuery.getPersonalChallengeById(challengeId);
		personalChallenge.addParticipation(memberId, timeUtils.now());
	}

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
			dto.challengeName(), dto.challengePoint(), dto.toBeginDate(), dto.toEndDate(), dto.challengeContent()
		);

		String beforeImageUrl = personalChallenge.getChallengeImage();
		personalChallenge.updateImage(dto.challengeImageUrl());
		fileManager.swapImage(beforeImageUrl, personalChallenge.getChallengeImage());
	}
}
