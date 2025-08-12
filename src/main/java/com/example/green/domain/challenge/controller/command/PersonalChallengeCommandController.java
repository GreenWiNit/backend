package com.example.green.domain.challenge.controller.command;

import static com.example.green.domain.challenge.controller.message.ChallengeResponseMessage.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.controller.command.docs.PersonalChallengeCommandControllerDocs;
import com.example.green.domain.challenge.controller.message.ChallengeResponseMessage;
import com.example.green.domain.challenge.service.PersonalChallengeService;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges/personal")
@RequiredArgsConstructor
@AuthenticatedApi
public class PersonalChallengeCommandController implements PersonalChallengeCommandControllerDocs {

	private final PersonalChallengeService challengeService;

	@PostMapping("/{challengeId}/participate")
	public NoContent join(@PathVariable Long challengeId, @AuthenticationPrincipal PrincipalDetails currentUser) {
		// todo: 동시성 이슈 해결
		Long memberId = currentUser.getMemberId();
		challengeService.join(challengeId, memberId);
		return NoContent.ok(CHALLENGE_JOINED);
	}

	@DeleteMapping("/{challengeId}/leave")
	public NoContent leave(@PathVariable Long challengeId, @AuthenticationPrincipal PrincipalDetails currentUser) {
		Long memberId = currentUser.getMemberId();
		challengeService.leave(challengeId, memberId);
		return NoContent.ok(ChallengeResponseMessage.CHALLENGE_LEFT);
	}
}
