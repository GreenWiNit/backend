package com.example.green.domain.challenge.repository.query;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;

public interface ChallengeGroupQuery {

	ChallengeGroup getChallengeGroup(Long groupId);

	void validateLeader(Long groupId, Long memberId);
}
