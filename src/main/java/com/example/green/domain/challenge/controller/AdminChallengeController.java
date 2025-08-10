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
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.service.AdminChallengeService;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeController implements AdminChallengeControllerDocs {

	private final AdminChallengeService adminChallengeService;
	private final ChallengeService challengeService;

	@PostMapping("/team")
	public ApiTemplate<Long> createTeamChallenge(@Valid @RequestBody AdminChallengeCreateRequestDto request) {
		Long challengeId = challengeService.createTeamChallenge(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@PostMapping("/personal")
	public ApiTemplate<Long> createPersonalChallenge(@Valid @RequestBody AdminChallengeCreateRequestDto request) {
		Long challengeId = challengeService.createPersonalChallenge(request);
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

	@PatchMapping("/{challengeId}/image")
	public ApiTemplate<AdminChallengeDetailDto> updateChallengeImage(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeImageUpdateRequestDto request) {
		AdminChallengeDetailDto result = adminChallengeService.updateChallengeImage(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_IMAGE_UPDATED, result);
	}

	@PatchMapping("/team/{challengeId}/visibility")
	public NoContent showTeamChallenge(@PathVariable Long challengeId) {
		challengeService.showTeamChallenge(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_SHOW);
	}

	@PatchMapping("/team/{challengeId}/invisibility")
	public NoContent hideTeamChallenge(@PathVariable Long challengeId) {
		challengeService.hideTeamChallenge(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_HIDE);
	}

	@PatchMapping("/personal/{challengeId}/visibility")
	public NoContent showPersonalChallenge(@PathVariable Long challengeId) {
		challengeService.showPersonalChallenge(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_SHOW);
	}

	@PatchMapping("/personal/{challengeId}/invisibility")
	public NoContent hidePersonalChallenge(@PathVariable Long challengeId) {
		challengeService.hidePersonalChallenge(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_HIDE);
	}
}
