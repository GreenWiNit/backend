package com.example.green.domain.challenge.controller.query;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeGroupDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.AdminChallengeGroupQueryControllerDocs;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeGroupQueryController implements AdminChallengeGroupQueryControllerDocs {

	private final ChallengeGroupQuery challengeGroupQuery;

	@GetMapping("/groups")
	public ApiTemplate<PageTemplate<AdminChallengeGroupDto>> getGroups(
		@RequestParam(required = false) Integer page,
		@RequestParam(required = false, defaultValue = "10") Integer size
	) {
		PageTemplate<AdminChallengeGroupDto> result = challengeGroupQuery.findGroupPaging(page, size);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_LIST_FOUND, result);
	}

	@GetMapping("/groups/{groupId}")
	public ApiTemplate<AdminTeamChallengeGroupDetailResponseDto> getGroupDetail(@PathVariable Long groupId) {
		//AdminTeamChallengeGroupDetailResponseDto result = adminChallengeService.getGroupDetail(groupId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_DETAIL_FOUND, null);
	}
}
