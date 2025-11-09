package com.example.green.domain.challenge.controller.query.v2;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.repository.query.ChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v2/challenges/personal")
@RestController
@AuthenticatedApi
public class PersonalChallengeQueryControllerV2 implements PersonalChallengeQueryControllerDocsV2 {

	private final ChallengeQuery challengeQuery;

	@GetMapping("/{challengeId}")
	public ApiTemplate<ChallengeDetailDtoV2> getPersonalChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		ChallengeDetailDtoV2 result = challengeQuery.findChallenge(challengeId, memberId);
		return ApiTemplate.ok(CHALLENGE_DETAIL_FOUND, result);
	}
}
