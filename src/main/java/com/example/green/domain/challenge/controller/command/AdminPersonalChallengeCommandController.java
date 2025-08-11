package com.example.green.domain.challenge.controller.command;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.AdminPersonalChallengeCommandControllerDocs;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeUpdateDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.service.PersonalChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/challenges/personal")
@RequiredArgsConstructor
public class AdminPersonalChallengeCommandController implements AdminPersonalChallengeCommandControllerDocs {

	private final PersonalChallengeService challengeService;

	@PostMapping
	public ApiTemplate<Long> create(@Valid @RequestBody AdminChallengeCreateDto request) {
		Long challengeId = challengeService.create(request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, challengeId);
	}

	@PutMapping("/{challengeId}")
	public ApiTemplate<Void> update(
		@PathVariable Long challengeId,
		@Valid @RequestBody AdminChallengeUpdateDto request
	) {
		challengeService.update(challengeId, request);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_UPDATED, null);
	}

	@PatchMapping("/{challengeId}/show")
	public NoContent show(@PathVariable Long challengeId) {
		challengeService.show(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_SHOW);
	}

	@PatchMapping("/{challengeId}/invisibility")
	public NoContent hide(@PathVariable Long challengeId) {
		challengeService.hide(challengeId);
		return NoContent.ok(AdminChallengeResponseMessage.CHALLENGE_HIDE);
	}
}
