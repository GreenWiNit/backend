package com.example.green.domain.challenge.infra;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChallengeGroupQueryImpl implements ChallengeGroupQuery {

	private final ChallengeGroupRepository challengeGroupRepository;

	@Override
	public ChallengeGroup getChallengeGroup(Long groupId) {
		return challengeGroupRepository.findById(groupId)
			.orElseThrow(() -> new ChallengeException(ChallengeExceptionMessage.CHALLENGE_GROUP_NOT_FOUND));
	}
}
