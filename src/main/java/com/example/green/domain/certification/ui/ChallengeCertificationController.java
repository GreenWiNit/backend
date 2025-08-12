package com.example.green.domain.certification.ui;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.certification.application.ChallengeCertificationService;
import com.example.green.domain.certification.application.command.PersonalChallengeCertificateCommand;
import com.example.green.domain.certification.application.command.TeamChallengeCertificateCommand;
import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.exception.CertificationException;
import com.example.green.domain.certification.exception.CertificationExceptionMessage;
import com.example.green.domain.certification.ui.docs.ChallengeCertificationControllerDocs;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDetailDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.domain.certification.ui.dto.PersonalChallengeCertificateDto;
import com.example.green.domain.certification.ui.dto.TeamChallengeCertificateDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/certifications/challenges")
@AuthenticatedApi
public class ChallengeCertificationController implements ChallengeCertificationControllerDocs {

	private final ChallengeCertificationService challengeCertificationService;
	private final ChallengeCertificationQuery challengeCertificationQuery;

	@PostMapping("/personal/{challengeId}")
	public NoContent certificateTeamChallenge(
		@PathVariable Long challengeId,
		@Valid @RequestBody PersonalChallengeCertificateDto dto,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		PersonalChallengeCertificateCommand command = dto.toCommand(memberId, challengeId);
		challengeCertificationService.certificatePersonalChallenge(command);
		return NoContent.ok(CertificationResponseMessage.PERSONAL_CHALLENGE_CERTIFICATE_SUCCESS);
	}

	@PostMapping("/team/{groupId}")
	public NoContent certificateTeamChallenge(
		@PathVariable Long groupId,
		@Valid @RequestBody TeamChallengeCertificateDto dto,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		TeamChallengeCertificateCommand command = dto.toCommand(memberId, groupId);
		challengeCertificationService.certificateTeamChallenge(command);
		return NoContent.ok(CertificationResponseMessage.TEAM_CHALLENGE_CERTIFICATE_SUCCESS);
	}

	@GetMapping("/me")
	public ApiTemplate<CursorTemplate<String, ChallengeCertificationDto>> getPersonalCertifications(
		@RequestParam(required = false, defaultValue = "P") String type,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false, defaultValue = "20") Integer size,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		if (!type.equals(ChallengeSnapshot.PERSONAL_TYPE) && !type.equals(ChallengeSnapshot.TEAM_TYPE)) {
			throw new CertificationException(CertificationExceptionMessage.INVALID_CHALLENGE_TYPE);
		}
		Long memberId = principalDetails.getMemberId();
		CursorTemplate<String, ChallengeCertificationDto> result =
			challengeCertificationQuery.findCertificationByPersonal(cursor, memberId, size, type);
		return ApiTemplate.ok(CertificationResponseMessage.CERTIFICATIONS_READ_SUCCESS, result);
	}

	@GetMapping("/{certificationId}")
	public ApiTemplate<ChallengeCertificationDetailDto> getCertificationDetail(
		@PathVariable Long certificationId,
		@AuthenticationPrincipal PrincipalDetails principalDetails
	) {
		Long memberId = principalDetails.getMemberId();
		ChallengeCertificationDetailDto result =
			challengeCertificationQuery.findCertificationDetail(certificationId, memberId);
		return ApiTemplate.ok(CertificationResponseMessage.CERTIFICATIONS_READ_SUCCESS, result);
	}
}
