package com.example.green.domain.challenge.service;

import static com.example.green.domain.common.sequence.SequenceType.*;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.repository.ChallengeRepository;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.infra.client.FileClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

	private final ChallengeRepository challengeRepository;
	private final Clock clock;
	private final FileClient fileClient;
	private final SequenceService sequenceService;

	public Long create(AdminChallengeCreateDto dto, ChallengeType challengeType) {
		String challengeCode = sequenceService.generateCode(PERSONAL_CHALLENGE, LocalDateTime.now(clock));
		Challenge challenge = dto.toChallenge(challengeCode, challengeType);
		Challenge savedChallenge = challengeRepository.save(challenge);
		fileClient.confirmUsingImage(savedChallenge.getImageUrl());

		return savedChallenge.getId();
	}

	@Retryable(
		retryFor = OptimisticLockingFailureException.class,
		backoff = @Backoff(delay = 100, multiplier = 2)
	)
	public void join(Long challengeId, Long memberId) {
		Challenge challenge = challengeRepository.getById(challengeId);
		challenge.participate(memberId);
	}

	public void show(Long challengeId) {
		Challenge challenge = challengeRepository.getById(challengeId);
		challenge.show();
	}

	public void hide(Long challengeId) {
		Challenge challenge = challengeRepository.getById(challengeId);
		challenge.hide();
	}

	public void update(Long challengeId, AdminChallengeUpdateDto dto) {
		Challenge challenge = challengeRepository.getById(challengeId);
		String beforeImageUrl = challenge.getImageUrl();

		challenge.updateInfo(dto.info());
		challenge.updateContent(dto.content());

		fileClient.swapImage(beforeImageUrl, challenge.getImageUrl());
	}
}
