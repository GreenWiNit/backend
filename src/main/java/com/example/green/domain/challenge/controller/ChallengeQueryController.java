package com.example.green.domain.challenge.controller;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;
import static com.example.green.domain.challenge.enums.ChallengeStatus.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.ChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.dto.ChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.message.ChallengeResponseMessage;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/challenges/")
@RestController
public class ChallengeQueryController implements ChallengeQueryControllerDocs {

	private final ChallengeService challengeService;
	private final PersonalChallengeRepository personalChallengeRepository;
	private final TeamChallengeRepository teamChallengeRepository;
	private final TimeUtils timeUtils;

	@GetMapping("/challenges/personal")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeListResponseDto> result =
			personalChallengeRepository.findPersonalChallengesByCursor(cursor, pageSize, PROCEEDING, timeUtils.now());

		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/challenges/team")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getTeamChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeListResponseDto> result =
			teamChallengeRepository.findTeamChallengesByCursor(cursor, pageSize, PROCEEDING, timeUtils.now());

		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/challenges/{chlgNo}")
	public ApiTemplate<ChallengeDetailResponseDto> getChallengeDetail(
		@PathVariable Long chlgNo,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		return ApiTemplate.ok(
			ChallengeResponseMessage.CHALLENGE_DETAIL_FOUND,
			challengeService.getChallengeDetail(chlgNo, currentUser)
		);
	}

	@GetMapping("/my/challenges/personal")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		return ApiTemplate.ok(
			ChallengeResponseMessage.MY_PERSONAL_CHALLENGE_LIST_FOUND,
			challengeService.getMyPersonalChallenges(cursor, currentUser)
		);
	}

	@GetMapping("/my/challenges/team")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyTeamChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		return ApiTemplate.ok(
			ChallengeResponseMessage.MY_TEAM_CHALLENGE_LIST_FOUND,
			challengeService.getMyTeamChallenges(cursor, currentUser)
		);
	}
}
