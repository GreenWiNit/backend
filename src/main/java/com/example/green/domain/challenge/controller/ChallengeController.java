package com.example.green.domain.challenge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.ChallengeControllerDocs;
import com.example.green.domain.challenge.controller.dto.ChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.message.ChallengeResponseMessage;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController implements ChallengeControllerDocs {

	private final ChallengeService challengeService;

	@GetMapping("/challenges/personal")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor
	) {
		return ApiTemplate.ok(
			ChallengeResponseMessage.CHALLENGE_LIST_FOUND,
			challengeService.getPersonalChallenges(cursor)
		);
	}

	@GetMapping("/challenges/team")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getTeamChallenges(
		@RequestParam(required = false) Long cursor
	) {
		return ApiTemplate.ok(
			ChallengeResponseMessage.CHALLENGE_LIST_FOUND,
			challengeService.getTeamChallenges(cursor)
		);
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

	@PostMapping("/challenges/{chlgNo}/participate")
	public NoContent joinChallenge(
		@PathVariable Long chlgNo,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		challengeService.joinChallenge(chlgNo, currentUser);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_JOINED);
	}

	@DeleteMapping("/challenges/{chlgNo}/participate")
	public NoContent leaveChallenge(
		@PathVariable Long chlgNo,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		challengeService.leaveChallenge(chlgNo, currentUser);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_LEFT);
	}
}
