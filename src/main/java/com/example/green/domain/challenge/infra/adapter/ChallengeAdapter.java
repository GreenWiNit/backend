package com.example.green.domain.challenge.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.ChallengeRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.domain.challenge.service.ChallengeGroupService;
import com.example.green.infra.client.ChallengeClient;
import com.example.green.infra.client.dto.ChallengeDto;
import com.example.green.infra.client.dto.ChallengeGroupDto;
import com.example.green.infra.client.request.CertificationConfirmRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChallengeAdapter implements ChallengeClient {

	private final ChallengeRepository challengeRepository;
	private final ChallengeGroupService challengeGroupService;
	private final ChallengeGroupQuery challengeGroupQuery;

	@Override
	public ChallengeDto getTeamChallenge(Long challengeId) {
		Challenge challenge = challengeRepository.findByIdWithThrow(challengeId);
		if (challenge.getType() != ChallengeType.TEAM) {
			throw new ChallengeException(ChallengeExceptionMessage.CHALLENGE_NOT_FOUND);
		}
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeDto getPersonalChallengeByMember(Long challengeId, Long memberId) {
		Challenge challenge = challengeRepository.findByIdWithThrow(challengeId);
		if (!challenge.isAlreadyParticipated(memberId)) {
			throw new ChallengeException(ChallengeExceptionMessage.NOT_PARTICIPATING_CHALLENGE);
		}
		return ChallengeDto.from(challenge);
	}

	@Override
	public ChallengeGroupDto getChallengeGroup(Long groupId, Long memberId) {
		challengeGroupQuery.validateMembership(groupId, memberId);
		ChallengeGroup challengeGroup = challengeGroupQuery.getChallengeGroup(groupId);
		return ChallengeGroupDto.from(challengeGroup);
	}

	@Override
	public void confirmTeamCertification(CertificationConfirmRequest request) {
		challengeGroupService.confirmTeamCertification(request.groupId(), request.memberId());
	}
}
