/*
package com.example.green.domain.challengecert.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challenge.entity.certification.CertificationStatus;
import com.example.green.domain.challengecert.controller.docs.AdminChallengeCertificationControllerDocs;
import com.example.green.domain.challengecert.controller.message.ChallengeCertificationResponseMessage;
import com.example.green.domain.challengecert.dto.AdminCertificationStatusUpdateRequestDto;
import com.example.green.domain.challengecert.dto.AdminChallengeTitleResponseDto;
import com.example.green.domain.challengecert.dto.AdminGroupCodeResponseDto;
import com.example.green.domain.challengecert.dto.AdminParticipantMemberKeyResponseDto;
import com.example.green.domain.challengecert.dto.AdminPersonalCertificationSearchRequestDto;
import com.example.green.domain.challengecert.dto.AdminTeamCertificationSearchRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.service.ChallengeCertificationService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

*/
/**
 * 관리자용 챌린지 인증 관리 컨트롤러
 *//*

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminChallengeCertificationController implements AdminChallengeCertificationControllerDocs {

	private final ChallengeCertificationService challengeCertificationService;

	@GetMapping("/challenges/personal-titles")
	public ApiTemplate<List<AdminChallengeTitleResponseDto>> getPersonalChallengeTitles() {
		List<AdminChallengeTitleResponseDto> response = challengeCertificationService.getPersonalChallengeTitles();
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_PERSONAL_CHALLENGE_TITLES_FOUND, response);
	}

	@GetMapping("/challenges/team-titles")
	public ApiTemplate<List<AdminChallengeTitleResponseDto>> getTeamChallengeTitles() {
		List<AdminChallengeTitleResponseDto> response = challengeCertificationService.getTeamChallengeTitles();
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_TEAM_CHALLENGE_TITLES_FOUND, response);
	}

	@GetMapping("/challenges/{challengeId}/participants-memberkeys")
	public ApiTemplate<List<AdminParticipantMemberKeyResponseDto>> getPersonalChallengeParticipantMemberKeys(
		@PathVariable Long challengeId
	) {
		List<AdminParticipantMemberKeyResponseDto> response =
			challengeCertificationService.getPersonalChallengeParticipantMemberKeys(challengeId);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_PARTICIPANT_MEMBER_KEYS_FOUND, response);
	}

	@GetMapping("/personal-certifications")
	public ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getPersonalCertificationsWithFilters(
		@RequestParam(required = false) Long challengeId,
		@RequestParam(required = false) String memberKey,
		@RequestParam(required = false) List<CertificationStatus> statuses,
		@RequestParam(required = false) Long cursor
	) {
		AdminPersonalCertificationSearchRequestDto searchRequest =
			new AdminPersonalCertificationSearchRequestDto(challengeId, memberKey, statuses, cursor);

		CursorTemplate<Long, ChallengeCertificationListResponseDto> response =
			challengeCertificationService.getPersonalCertificationsWithFilters(searchRequest);

		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_PERSONAL_CERTIFICATIONS_WITH_FILTERS_FOUND,
			response);
	}

	@GetMapping("/challenges/{challengeId}/group-codes")
	public ApiTemplate<List<AdminGroupCodeResponseDto>> getTeamChallengeGroupCodes(
		@PathVariable Long challengeId
	) {
		List<AdminGroupCodeResponseDto> response =
			challengeCertificationService.getTeamChallengeGroupCodes(challengeId);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_GROUP_CODES_FOUND, response);
	}

	@GetMapping("/team-certifications")
	public ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getTeamCertificationsWithFilters(
		@RequestParam(required = false) Long challengeId,
		@RequestParam(required = false) String groupCode,
		@RequestParam(required = false) List<CertificationStatus> statuses,
		@RequestParam(required = false) Long cursor
	) {
		AdminTeamCertificationSearchRequestDto searchRequest =
			new AdminTeamCertificationSearchRequestDto(challengeId, groupCode, statuses, cursor);

		CursorTemplate<Long, ChallengeCertificationListResponseDto> response =
			challengeCertificationService.getTeamCertificationsWithFilters(searchRequest);

		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_TEAM_CERTIFICATIONS_WITH_FILTERS_FOUND,
			response);
	}

	@PatchMapping("/certifications/{certificationId}")
	public ApiTemplate<Void> updateCertificationStatus(
		@PathVariable Long certificationId,
		@Valid @RequestBody AdminCertificationStatusUpdateRequestDto request
	) {
		challengeCertificationService.updateCertificationStatus(certificationId, request.status().name());
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.ADMIN_CERTIFICATION_STATUS_UPDATED);
	}
}
*/
