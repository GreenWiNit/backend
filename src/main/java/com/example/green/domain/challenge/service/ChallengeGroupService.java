package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupCreateDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.global.utils.TimeUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeGroupService {

	private final TimeUtils timeUtils;
	private final SequenceService sequenceService;
	private final ChallengeGroupRepository challengeGroupRepository;

	public Long create(Long challengeId, Long leaderId, ChallengeGroupCreateDto dto) {
		String teamCode = sequenceService.generateCode(SequenceType.TEAM_CHALLENGE_GROUP, timeUtils.now());
		ChallengeGroup challengeGroup = dto.toEntity(teamCode, challengeId, leaderId);
		ChallengeGroup savedChallengeGroup = challengeGroupRepository.save(challengeGroup);
		return savedChallengeGroup.getId();
	}

	public void update(Long groupId, @Valid TeamChallengeGroupUpdateRequestDto request, Long memberId) {
	}

	public void delete(Long groupId, Long memberId) {
	}

	public void join(Long groupId, Long memberId) {

	}
}
