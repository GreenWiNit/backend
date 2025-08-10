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
import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/challenges/")
@RestController
public class ChallengeQueryController implements ChallengeQueryControllerDocs {

	private final PersonalChallengeQuery personalChallengeQuery;
	private final TeamChallengeQuery teamChallengeQuery;
	private final TimeUtils timeUtils;

	@GetMapping("/challenges/personal")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeListResponseDto> result =
			personalChallengeQuery.findPersonalChallengesByCursor(cursor, pageSize, PROCEEDING, timeUtils.now());

		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/challenges/team")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getTeamChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeListResponseDto> result =
			teamChallengeQuery.findTeamChallengesByCursor(cursor, pageSize, PROCEEDING, timeUtils.now());

		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@Deprecated
	@GetMapping("/challenges/{chlgNo}")
	public ApiTemplate<ChallengeDetailDto> getChallengeDetail(@PathVariable Long chlgNo) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	@GetMapping("/challenges/personal/{chlgNo}")
	public ApiTemplate<ChallengeDetailDto> getPersonalChallenge(
		@PathVariable(value = "chlgNo") Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		ChallengeDetailDto result = personalChallengeQuery.findPersonalChallenge(challengeId, memberId);
		return ApiTemplate.ok(CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/challenges/team/{chlgNo}")
	public ApiTemplate<ChallengeDetailDto> getTeamChallenge(
		@PathVariable(value = "chlgNo") Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		ChallengeDetailDto result = teamChallengeQuery.findTeamChallenge(challengeId, memberId);
		return ApiTemplate.ok(CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/my/challenges/personal")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		Long memberId = currentUser.getMemberId();
		CursorTemplate<Long, ChallengeListResponseDto> result =
			personalChallengeQuery.findMyParticipationByCursor(memberId, cursor, pageSize);

		return ApiTemplate.ok(MY_PERSONAL_CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/my/challenges/team")
	public ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyTeamChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		Long memberId = currentUser.getMemberId();
		CursorTemplate<Long, ChallengeListResponseDto> result =
			teamChallengeQuery.findMyParticipationByCursor(memberId, cursor, pageSize);

		return ApiTemplate.ok(MY_TEAM_CHALLENGE_LIST_FOUND, result);
	}
}
