package com.example.green.domain.challenge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.AdminChallengeControllerDocs;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeGroupListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeListResponseDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.service.AdminChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeController implements AdminChallengeControllerDocs {

	private final AdminChallengeService adminChallengeService;

	@PostMapping("/team")
	public ApiTemplate<Long> createTeamChallenge(@Valid @RequestBody AdminChallengeCreateRequestDto request) {
		Long challengeId = adminChallengeService.createTeamChallenge(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@PostMapping("/personal")
	public ApiTemplate<Long> createPersonalChallenge(@Valid @RequestBody AdminChallengeCreateRequestDto request) {
		Long challengeId = adminChallengeService.createPersonalChallenge(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@Override
	@PutMapping("/{challengeId}")
	public ApiTemplate<Void> updateChallenge(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeUpdateRequestDto request) {
		adminChallengeService.updateChallenge(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_UPDATED, null);
	}

	@Override
	@PatchMapping("/{challengeId}/image")
	public ApiTemplate<AdminChallengeDetailResponseDto> updateChallengeImage(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeImageUpdateRequestDto request) {
		AdminChallengeDetailResponseDto result = adminChallengeService.updateChallengeImage(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_IMAGE_UPDATED, result);
	}

	@Override
	@PatchMapping("/{challengeId}/visibility")
	public ApiTemplate<Void> updateChallengeDisplayStatus(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeDisplayStatusUpdateRequestDto request) {
		adminChallengeService.updateChallengeDisplayStatus(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_DISPLAY_STATUS_UPDATED, null);
	}

	@Override
	@GetMapping("/personal")
	public ApiTemplate<CursorTemplate<Long, AdminPersonalChallengeListResponseDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor) {
		CursorTemplate<Long, AdminPersonalChallengeListResponseDto> result
			= adminChallengeService.getPersonalChallenges(cursor);
		return ApiTemplate.ok(AdminChallengeResponseMessage.PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@Override
	@GetMapping("/team")
	public ApiTemplate<CursorTemplate<Long, AdminTeamChallengeListResponseDto>> getTeamChallenges(
		@RequestParam(required = false) Long cursor) {
		CursorTemplate<Long, AdminTeamChallengeListResponseDto> result =
			adminChallengeService.getTeamChallenges(cursor);
		return ApiTemplate.ok(AdminChallengeResponseMessage.TEAM_CHALLENGE_LIST_FOUND, result);
	}

	@Override
	@GetMapping("/{challengeId}")
	public ApiTemplate<AdminChallengeDetailResponseDto> getChallengeDetail(@PathVariable Long challengeId) {
		AdminChallengeDetailResponseDto result = adminChallengeService.getChallengeDetail(challengeId);
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
