package com.example.green.domain.challenge.controller.command;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.docs.ChallengeControllerDocs;
import com.example.green.domain.challenge.controller.message.ChallengeResponseMessage;
import com.example.green.domain.challenge.service.ChallengeService;
import com.example.green.global.api.NoContent;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController implements ChallengeControllerDocs {

	private final ChallengeService challengeService;

	@Deprecated
	@PostMapping("/challenges/{chlgNo}/participate")
	public NoContent joinChallenge(@PathVariable Long chlgNo) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	@PostMapping("/challenges/team/{challengeId}/participate")
	public NoContent joinTeamChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		Long memberId = 1L;
		challengeService.joinTeamChallenge(challengeId, memberId);
		return NoContent.ok(CHALLENGE_JOINED);
	}

	@PostMapping("/challenges/personal/{challengeId}/participate")
	public NoContent joinPersonalChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		Long memberId = 1L;
		challengeService.joinPersonalChallenge(challengeId, memberId);
		return NoContent.ok(CHALLENGE_JOINED);
	}

	@Deprecated
	@DeleteMapping("/challenges/{chlgNo}/leave")
	public NoContent leaveChallenge(@PathVariable Long chlgNo) {
		throw new BusinessException(GlobalExceptionMessage.NO_RESOURCE_MESSAGE);
	}

	@DeleteMapping("/challenges/personal/{challengeId}/leave")
	public NoContent leavePersonalChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		Long memberId = 1L;
		challengeService.leavePersonalChallenge(challengeId, memberId);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_LEFT);
	}

	@DeleteMapping("/challenges/team/{challengeId}/participate")
	public NoContent leaveTeamChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		Long memberId = 1L;
		challengeService.leaveTeamChallenge(challengeId, memberId);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_LEFT);
	}
}
