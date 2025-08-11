package com.example.green.domain.challenge.controller.command;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.TeamChallengeCommandControllerDocs;
import com.example.green.domain.challenge.controller.message.ChallengeResponseMessage;
import com.example.green.domain.challenge.service.TeamChallengeService;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges/team")
@RequiredArgsConstructor
public class TeamChallengeCommandController implements TeamChallengeCommandControllerDocs {

	private final TeamChallengeService challengeService;

	@PostMapping("/{challengeId}/participate")
	public NoContent joinTeamChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		// todo: 동시성 이슈 해결
		Long memberId = 2L;
		challengeService.join(challengeId, memberId);
		return NoContent.ok(CHALLENGE_JOINED);
	}

	@DeleteMapping("/{challengeId}/leave")
	public NoContent leaveTeamChallenge(
		@PathVariable Long challengeId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		Long memberId = 1L;
		challengeService.leave(challengeId, memberId);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_LEFT);
	}
}
