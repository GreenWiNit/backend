package com.example.green.domain.challenge.controller.query;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;
import static com.example.green.domain.challenge.entity.challenge.vo.ChallengeType.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.query.docs.PersonalChallengeQueryControllerDocs;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.repository.query.ChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/challenges/personal")
@RestController
public class PersonalChallengeQueryController implements PersonalChallengeQueryControllerDocs {

	private final ChallengeQuery challengeQuery;

	@GetMapping
	@PublicApi
	public ApiTemplate<CursorTemplate<Long, ChallengeDto>> getPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		CursorTemplate<Long, ChallengeDto> result = challengeQuery.findChallengesByCursor(cursor, pageSize, PERSONAL);
		return ApiTemplate.ok(CHALLENGE_LIST_FOUND, result);
	}

	@GetMapping("/{challengeId}")
	@Deprecated
	public ApiTemplate<ChallengeDetailDto> getPersonalChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	@GetMapping("/me")
	@AuthenticatedApi
	public ApiTemplate<CursorTemplate<Long, ChallengeDto>> getMyPersonalChallenges(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser,
		@RequestParam(required = false, defaultValue = "20") Integer pageSize
	) {
		Long memberId = currentUser.getMemberId();
		CursorTemplate<Long, ChallengeDto> result =
			challengeQuery.findMyParticipationByCursor(memberId, cursor, pageSize, PERSONAL);
		return ApiTemplate.ok(MY_PERSONAL_CHALLENGE_LIST_FOUND, result);
	}
}
