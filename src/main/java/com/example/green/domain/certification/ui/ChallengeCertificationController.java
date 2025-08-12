package com.example.green.domain.certification.ui;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.certification.application.ChallengeCertificationService;
import com.example.green.domain.certification.application.command.PersonalChallengeCertificateCommand;
import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;
import com.example.green.domain.certification.ui.dto.PersonalChallengeCertificateDto;
import com.example.green.domain.certification.ui.dto.TeamChallengeCertificateDto;
import com.example.green.global.api.NoContent;
import com.example.green.global.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certifications/challenges")
public class ChallengeCertificationController {

	private final ChallengeCertificationService challengeCertificationService;

	@PostMapping("/personal/{challengeId}")
	public NoContent certificateTeamChallenge(
		@PathVariable Long challengeId,
		@RequestBody PersonalChallengeCertificateDto dto,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		PersonalChallengeCertificateCommand command = dto.toCommand(memberId, challengeId);
		challengeCertificationService.certificatePersonalChallenge(command);
		return NoContent.ok(CertificationResponseMessage.TEAM_CHALLENGE_CERTIFICATE_SUCCESS);
	}

	@PostMapping("/team/{groupId}")
	public NoContent certificateTeamChallenge(
		@PathVariable Long groupId,
		@RequestBody TeamChallengeCertificateDto dto,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		TeamChallengeCertificateCommand command = dto.toCommand(memberId, groupId);
		challengeCertificationService.certificateTeamChallenge(command);
		return NoContent.ok(CertificationResponseMessage.TEAM_CHALLENGE_CERTIFICATE_SUCCESS);
	}
}
