package com.example.green.domain.challenge.controller;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.AdminChallengeControllerDocs;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges")
@RequiredArgsConstructor
public class AdminChallengeController implements AdminChallengeControllerDocs {

	private final ChallengeService challengeService;

	@PostMapping("/team")
	public ApiTemplate<Long> createTeamChallenge(@Valid @RequestBody AdminChallengeCreateDto request) {
		Long challengeId = challengeService.createTeamChallenge(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@PostMapping("/personal")
	public ApiTemplate<Long> createPersonalChallenge(@Valid @RequestBody AdminChallengeCreateDto request) {
		Long challengeId = challengeService.createPersonalChallenge(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@PutMapping("/personal/{challengeId}")
	public ApiTemplate<Void> updatePersonalChallenge(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeUpdateDto request
	) {
		challengeService.updatePersonalChallenge(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_UPDATED, null);
	}

	@PutMapping("/team/{challengeId}")
	public ApiTemplate<Void> updateTeamChallenge(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeUpdateDto request
	) {
		challengeService.updateTeamChallenge(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_UPDATED, null);
	}

	@Deprecated
	@PatchMapping("/{challengeId}/image")
	public ApiTemplate<AdminChallengeDetailDto> updateChallengeImage(@PathVariable Long challengeId) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
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
