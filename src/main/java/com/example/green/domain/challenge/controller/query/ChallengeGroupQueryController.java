package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.TeamChallengeGroupResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.dto.ChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.message.TeamChallengeGroupResponseMessage;
import com.example.green.domain.challenge.controller.query.docs.ChallengeGroupQueryControllerDocs;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeGroupQueryController implements ChallengeGroupQueryControllerDocs {

	private final ChallengeGroupQuery challengeGroupQuery;

	@GetMapping("/{challengeId}/groups/me")
	public ApiTemplate<CursorTemplate<String, ChallengeGroupDto>> getTeamChallengeGroups(
		@PathVariable Long challengeId,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		CursorTemplate<String, ChallengeGroupDto> result =
			challengeGroupQuery.findMyGroup(challengeId, cursor, size, memberId);

		return ApiTemplate.ok(MY_TEAM_GROUP_FOUND, result);
	}

	@GetMapping("/groups/{groupId}")
	public ApiTemplate<ChallengeGroupDetailDto> getTeamChallengeGroupDetail(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		ChallengeGroupDetailDto result = challengeGroupQuery.getGroupDetail(groupId, memberId);
		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_DETAIL_FOUND, result);
	}

}
