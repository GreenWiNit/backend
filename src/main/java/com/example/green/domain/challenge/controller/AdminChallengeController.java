package com.example.green.domain.challenge.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.AdminChallengeControllerDocs;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDisplayStatusUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.service.AdminChallengeService;
import com.example.green.global.api.ApiTemplate;

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
}
