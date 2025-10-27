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
import com.example.green.domain.challenge.service.PersonalChallengeService;
import com.example.green.domain.challenge.service.TeamChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeCommandController implements ChallengeCommandControllerDocs {

	private final PersonalChallengeService personalChallengeService;
	private final TeamChallengeService teamChallengeService;

	@PostMapping("/{type}")
	@PublicApi
	public ApiTemplate<Long> create(@PathVariable ChallengeType type, @RequestBody AdminChallengeCreateDto dto) {
		if (type == ChallengeType.PERSONAL) {
			Long result = personalChallengeService.create(dto);
			return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, result);
		}
		Long result = teamChallengeService.create(dto);
		return ApiTemplate.ok(AdminChallengeResponseMessage.CHALLENGE_CREATED, result);
	}
}
