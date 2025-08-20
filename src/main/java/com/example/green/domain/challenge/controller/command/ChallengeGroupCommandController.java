package com.example.green.domain.challenge.controller.command;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.ChallengeGroupCommandControllerDocs;
import com.example.green.domain.challenge.controller.command.dto.ChallengeGroupCreateDto;
import com.example.green.domain.challenge.controller.command.dto.ChallengeGroupUpdateDto;
import com.example.green.domain.challenge.controller.message.TeamChallengeGroupResponseMessage;
import com.example.green.domain.challenge.service.ChallengeGroupService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
@AuthenticatedApi
public class ChallengeGroupCommandController implements ChallengeGroupCommandControllerDocs {

	private final ChallengeGroupService challengeGroupService;

	@PostMapping("/{challengeId}/groups")
	public ApiTemplate<Long> createTeamChallengeGroup(
		@PathVariable Long challengeId,
		@Valid @RequestBody ChallengeGroupCreateDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long leaderId = principalDetails.getMemberId();
		Long groupId = challengeGroupService.create(challengeId, leaderId, request);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_CREATED, groupId);
	}

	@PutMapping("/groups/{groupId}")
	@Deprecated
	public NoContent updateTeamChallengeGroup(
		@PathVariable Long groupId,
		@Valid @RequestBody ChallengeGroupUpdateDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	@DeleteMapping("/groups/{groupId}")
	public NoContent deleteTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		challengeGroupService.delete(groupId, memberId);
		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_DELETED);
	}

	@PostMapping("/groups/{groupId}")
	public NoContent joinTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		challengeGroupService.join(groupId, memberId);
		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_JOINED);
	}
}
