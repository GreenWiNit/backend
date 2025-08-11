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
import com.example.green.global.security.PrincipalDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeGroupCommandController implements ChallengeGroupCommandControllerDocs {

	private final ChallengeGroupService challengeGroupService;

	@PostMapping("/{challengeId}/groups")
	public ApiTemplate<Long> createTeamChallengeGroup(
		@PathVariable Long challengeId,
		@Valid @RequestBody ChallengeGroupCreateDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		// todo: 오늘 날짜로 가입하거나 등록한 팀이 있으면 더 생성 불가
		Long leaderId = 1L;
		Long groupId = challengeGroupService.create(challengeId, leaderId, request);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_CREATED, groupId);
	}

	@PutMapping("/groups/{groupId}")
	public NoContent updateTeamChallengeGroup(
		@PathVariable Long groupId,
		@Valid @RequestBody ChallengeGroupUpdateDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long leaderId = 1L;
		challengeGroupService.update(groupId, leaderId, request);
		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_UPDATED);
	}

	@DeleteMapping("/groups/{groupId}")
	public NoContent deleteTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		challengeGroupService.delete(groupId, memberId);
		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_DELETED);
	}

	@PostMapping("/groups/{groupId}")
	public NoContent joinTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		// todo: 오늘 날짜로 가입하거나 등록한 팀이 있으면 가입 불가
		Long memberId = 2L;
		challengeGroupService.join(groupId, memberId);
		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_JOINED);
	}
}
