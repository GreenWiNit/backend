package com.example.green.domain.challenge.controller.command;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.ChallengeCommandControllerDocs;
import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.message.AdminChallengeResponseMessage;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeCommandController implements ChallengeCommandControllerDocs {

	private final ChallengeService challengeService;

	@PostMapping("/{type}")
	@AuthenticatedApi
	public ApiTemplate<Long> create(@PathVariable ChallengeType type, @RequestBody AdminChallengeCreateDto dto) {
		Long result = challengeService.create(dto, type);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, result);
	}
}
