package com.example.green.domain.challenge.repository.query;

import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.MyChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface ChallengeGroupQuery {

	ChallengeGroup getChallengeGroup(Long groupId);

	void validateLeader(Long groupId, Long memberId);

	CursorTemplate<String, MyChallengeGroupDto> findMyGroup(Long challengeId, String cursor, Integer size,
		Long memberId);

	ChallengeGroupDetailDto getGroupDetail(Long groupId, Long memberId);

	CursorTemplate<String, ChallengeGroupDto> findAllGroupByCursor(
		Long challengeId, String cursor, Integer size, Long memberId);

	PageTemplate<AdminChallengeGroupDto> findGroupPaging(Integer page, Integer size);

	AdminChallengeGroupDetailDto getGroupDetailForAdmin(Long groupId);
}
