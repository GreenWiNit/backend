package com.example.green.domain.challenge.controller.query.v2;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v2/challenges/team")
@RestController
@AuthenticatedApi
public class TeamChallengeQueryControllerV2 implements TeamChallengeQueryControllerDocsV2 {

	private final TeamChallengeQuery teamChallengeQuery;

	@GetMapping("/{challengeId}")
	public ApiTemplate<ChallengeDetailDtoV2> getTeamChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		ChallengeDetailDtoV2 result = teamChallengeQuery.findTeamChallengeV2(challengeId, memberId);
		return ApiTemplate.ok(CHALLENGE_DETAIL_FOUND, result);
	}
}
