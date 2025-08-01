package com.example.green.domain.challenge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.TeamChallengeGroupControllerDocs;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.TeamChallengeGroupUpdateRequestDto;
import com.example.green.domain.challenge.controller.message.TeamChallengeGroupResponseMessage;
import com.example.green.domain.challenge.service.TeamChallengeGroupService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class TeamChallengeGroupController implements TeamChallengeGroupControllerDocs {

	private final TeamChallengeGroupService teamChallengeGroupService;

	@Override
	@GetMapping("/{challengeId}/groups")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 목록 조회는 로그인한 사용자만 가능합니다")
	public ApiTemplate<CursorTemplate<Long, TeamChallengeGroupListResponseDto>> getTeamChallengeGroups(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		CursorTemplate<Long, TeamChallengeGroupListResponseDto> result
			= teamChallengeGroupService.getTeamChallengeGroups(challengeId, cursor, memberId);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_LIST_FOUND, result);
	}

	@Override
	@PostMapping("/{challengeId}/groups")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 생성은 로그인한 사용자만 가능합니다")
	public ApiTemplate<Long> createTeamChallengeGroup(
		@PathVariable Long challengeId,
		@Valid @RequestBody TeamChallengeGroupCreateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		Long groupId = teamChallengeGroupService.createTeamChallengeGroup(
			challengeId, request, memberId
		);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_CREATED, groupId);
	}

	@Override
	@GetMapping("/groups/{groupId}")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 상세 조회는 로그인한 사용자만 가능합니다")
	public ApiTemplate<TeamChallengeGroupDetailResponseDto> getTeamChallengeGroupDetail(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		TeamChallengeGroupDetailResponseDto result =
			teamChallengeGroupService.getTeamChallengeGroupDetail(groupId, memberId);

		return ApiTemplate.ok(TeamChallengeGroupResponseMessage.GROUP_DETAIL_FOUND, result);
	}

	@Override
	@PostMapping("/groups/{groupId}")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 참가는 로그인한 사용자만 가능합니다")
	public NoContent joinTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		teamChallengeGroupService.joinTeamChallengeGroup(groupId, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_JOINED);
	}

	@Override
	@PutMapping("/groups/{groupId}")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 수정은 로그인한 사용자만 가능합니다")
	public NoContent updateTeamChallengeGroup(
		@PathVariable Long groupId,
		@Valid @RequestBody TeamChallengeGroupUpdateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		teamChallengeGroupService.updateTeamChallengeGroup(groupId, request, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_UPDATED);
	}

	@Override
	@DeleteMapping("/groups/{groupId}")
	@AuthenticatedApi(reason = "팀 챌린지 그룹 삭제는 로그인한 사용자만 가능합니다")
	public NoContent deleteTeamChallengeGroup(
		@PathVariable Long groupId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId;
		if (principalDetails != null) {
			memberId = principalDetails.getMemberId();
		} else {
			memberId = null;
		}

		teamChallengeGroupService.deleteTeamChallengeGroup(groupId, memberId);

		return NoContent.ok(TeamChallengeGroupResponseMessage.GROUP_DELETED);
	}
}
