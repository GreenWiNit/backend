package com.example.green.domain.challenge.controller;

import static com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.AdminChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengesDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.domain.challenge.service.AdminChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeQueryController implements AdminChallengeQueryControllerDocs {

	private final AdminChallengeService adminChallengeService;
	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;

	@GetMapping("/personal")
	public ApiTemplate<CursorTemplate<Long, AdminPersonalChallengesDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		CursorTemplate<Long, AdminPersonalChallengesDto> result =
			personalChallengeQuery.findAllForAdminByCursor(cursor, size);
		return ApiTemplate.ok(PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/team")
	public ApiTemplate<CursorTemplate<Long, AdminTeamChallengesDto>> getTeamChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size
	) {
		CursorTemplate<Long, AdminTeamChallengesDto> result = teamChallengeQuery.findAllForAdminByCursor(cursor, size);
		return ApiTemplate.ok(TEAM_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/personal/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getPersonalChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = personalChallengeQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/team/{challengeId}")
	public ApiTemplate<AdminChallengeDetailDto> getTeamChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailDto result = teamChallengeQuery.getChallengeDetail(challengeId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DETAIL_FOUND, result);
	}

	@Override
	@GetMapping("/{challengeId}/participants")
	public ApiTemplate<CursorTemplate<Long, AdminChallengeParticipantListResponseDto>> getChallengeParticipants(
		@PathVariable Long challengeId,
		@RequestParam(required = false) Long cursor) {
		CursorTemplate<Long, AdminChallengeParticipantListResponseDto> result
			= adminChallengeService.getChallengeParticipants(challengeId, cursor);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_PARTICIPANTS_FOUND, result);
	}

	@Override
	@GetMapping("/groups")
	public ApiTemplate<CursorTemplate<Long, AdminTeamChallengeGroupListResponseDto>> getGroups(
		@RequestParam(required = false) Long cursor) {
		CursorTemplate<Long, AdminTeamChallengeGroupListResponseDto> result = adminChallengeService.getGroups(cursor);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_LIST_FOUND, result);
	}

	@Override
	@GetMapping("/groups/{groupId}")
	public ApiTemplate<AdminTeamChallengeGroupDetailResponseDto> getGroupDetail(@PathVariable Long groupId) {
		AdminTeamChallengeGroupDetailResponseDto result = adminChallengeService.getGroupDetail(groupId);
		return ApiTemplate.ok(AdminChallengeResponseMessage.GROUP_DETAIL_FOUND, result);
	}
}
