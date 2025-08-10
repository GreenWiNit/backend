package com.example.green.domain.challenge.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupCreateDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.common.sequence.SequenceService;
import com.example.green.domain.common.sequence.SequenceType;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeGroupService {

	private final TimeUtils timeUtils;
	private final SequenceService sequenceService;
	private final ChallengeGroupQuery challengeGroupQuery;
	private final ChallengeGroupRepository challengeGroupRepository;

	public Long create(Long challengeId, Long leaderId, ChallengeGroupCreateDto dto) {
		String teamCode = sequenceService.generateCode(SequenceType.TEAM_CHALLENGE_GROUP, timeUtils.now());
		ChallengeGroup challengeGroup = dto.toEntity(teamCode, challengeId, leaderId);
		ChallengeGroup savedChallengeGroup = challengeGroupRepository.save(challengeGroup);
		return savedChallengeGroup.getId();
	}

	public void update(Long groupId, Long leaderId, TeamChallengeGroupUpdateRequestDto request) {
		ChallengeGroup group = challengeGroupQuery.getChallengeGroup(groupId);
		if (!group.isLeader(leaderId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_GROUP_LEADER);
		}
		group.updateBasicInfo(request.toBasicinfo());
		group.updateAddress(request.toAddress());
		group.updatePeriod(request.toPeriod());
		group.updateCapacity(request.maxParticipants());
	}

	public void delete(Long groupId, Long memberId) {
		challengeGroupQuery.validateLeader(groupId, memberId);
		challengeGroupRepository.deleteById(groupId);
	}

	public void join(Long groupId, Long memberId) {

	}
}
