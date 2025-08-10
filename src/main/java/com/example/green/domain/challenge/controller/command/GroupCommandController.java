package com.example.green.domain.challenge.controller.command;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.GroupCommandControllerDocs;
import com.example.green.domain.challenge.controller.dto.GroupCreateDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.controller.message.TeamChallengeGroupResponseMessage;
import com.example.green.domain.challenge.service.GroupService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class GroupCommandController implements GroupCommandControllerDocs {

	private final GroupService groupService;

	@PostMapping("/{challengeId}/groups")
	public ApiTemplate<Long> createTeamChallengeGroup(
		@PathVariable Long challengeId,
		@Valid @RequestBody GroupCreateDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		Long groupId = groupService.create(challengeId, memberId, request);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_CREATED, groupId);
	}

	@PutMapping("/groups/{groupId}")
	public NoContent updateTeamChallengeGroup(
		@PathVariable Long groupId,
		@Valid @RequestBody TeamChallengeGroupUpdateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		groupService.update(groupId, request, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_UPDATED);
	}

	@DeleteMapping("/groups/{groupId}")
	public NoContent deleteTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		groupService.delete(groupId, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_DELETED);
	}

	@PostMapping("/groups/{groupId}")
	public NoContent joinTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		groupService.join(groupId, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_JOINED);
	}
}
