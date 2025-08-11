package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;
import static com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.controller.query.docs.PersonalChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.utils.TimeUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/challenges/personal")
@RestController
public class PersonalChallengeQueryController implements PersonalChallengeQueryControllerDocs {

	private final PersonalChallengeQuery personalChallengeQuery;
	private final TimeUtils timeUtils;

	@GetMapping
	public ApiTemplate<CursorTemplate<Long, ChallengeDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeDto> result =
			personalChallengeQuery.findPersonalChallengesByCursor(cursor, pageSize, PROCEEDING, timeUtils.now());

		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/{challengeId}")
	public ApiTemplate<ChallengeDetailDto> getPersonalChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = 1L;
		ChallengeDetailDto result = personalChallengeQuery.findPersonalChallenge(challengeId, memberId);
		return ApiTemplate.ok(CHALLENGE_DETAIL_FOUND, result);
	}

	@GetMapping("/me")
	public ApiTemplate<CursorTemplate<Long, ChallengeDto>> getMyPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		Long memberId = 1L;
		CursorTemplate<Long, ChallengeDto> result =
			personalChallengeQuery.findMyParticipationByCursor(memberId, cursor, pageSize);

		return ApiTemplate.ok(MY_PERSONAL_CHALLENGE_LIST_FOUND, result);
	}
}
