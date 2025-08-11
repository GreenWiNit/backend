package com.example.green.domain.challenge.repository.query;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.dto.MyChallengeGroupDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.global.api.page.CursorTemplate;

public interface ChallengeGroupQuery {

	ChallengeGroup getChallengeGroup(Long groupId);

	void validateLeader(Long groupId, Long memberId);

	CursorTemplate<String, MyChallengeGroupDto> findMyGroup(Long challengeId, String cursor, Integer size,
		Long memberId);

	ChallengeGroupDetailDto getGroupDetail(Long groupId, Long memberId);

	CursorTemplate<String, ChallengeGroupDto> findAllGroupByCursor(
		Long challengeId, String cursor, Integer size, Long memberId);
}
