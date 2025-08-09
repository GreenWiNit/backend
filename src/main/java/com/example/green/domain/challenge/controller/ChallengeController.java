package com.example.green.domain.challenge.controller;

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
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeController implements ChallengeControllerDocs {

	private final ChallengeService challengeService;

	@PostMapping("/challenges/{chlgNo}/participate")
	public NoContent joinChallenge(
		@PathVariable Long chlgNo,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		challengeService.joinChallenge(chlgNo, currentUser);
		return NoContent.ok(CHALLENGE_JOINED);
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
