package com.example.green.domain.challenge.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.command.dto.ChallengeGroupCreateDto;
import com.example.green.domain.challenge.controller.command.dto.ChallengeGroupUpdateDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
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
	private final TeamChallengeQuery teamChallengeQuery;
	private final ChallengeGroupRepository challengeGroupRepository;

	public Long create(Long challengeId, Long leaderId, ChallengeGroupCreateDto dto) {
		teamChallengeQuery.validateGroupPeriod(challengeId, dto.beginDateTime(), dto.endDateTime());
		String teamCode = sequenceService.generateCode(SequenceType.TEAM_CHALLENGE_GROUP, timeUtils.now());
		ChallengeGroup challengeGroup = dto.toEntity(teamCode, challengeId, leaderId);
		ChallengeGroup savedChallengeGroup = challengeGroupRepository.save(challengeGroup);
		return savedChallengeGroup.getId();
	}

	public void update(Long groupId, Long leaderId, ChallengeGroupUpdateDto request) {
		ChallengeGroup group = challengeGroupQuery.getChallengeGroup(groupId);
		if (!group.isLeader(leaderId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_GROUP_LEADER);
		}
		group.updateBasicInfo(request.toBasicInfo());
		group.updateAddress(request.toAddress());
		group.updatePeriod(request.toPeriod());
		group.updateCapacity(request.maxParticipants());
	}

	public void delete(Long groupId, Long memberId) {
		challengeGroupQuery.validateLeader(groupId, memberId);
		challengeGroupRepository.deleteById(groupId);
	}

	@Retryable(retryFor = OptimisticLockingFailureException.class)
	public void join(Long groupId, Long memberId) {
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId);
		challengeGroup.joinMember(memberId, timeUtils.now());
	}

	@Retryable(retryFor = OptimisticLockingFailureException.class)
	public void leave(Long groupId, Long memberId) {
		// todo: 나중에 사용할 수도 명확히 모름
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId);
		challengeGroup.leaveMember(memberId);
	}
}
