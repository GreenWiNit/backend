/*
package com.example.green.domain.challengecert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challengecert.controller.docs.ChallengeCertificationControllerDocs;
import com.example.green.domain.challengecert.controller.message.ChallengeCertificationResponseMessage;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationDetailResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.service.ChallengeCertificationService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChallengeCertificationController implements ChallengeCertificationControllerDocs {

	private final ChallengeCertificationService challengeCertificationService;

	@PostMapping("/challenges/{challengeId}/certifications")
	@AuthenticatedApi(reason = "챌린지 인증 등록은 로그인한 사용자만 가능합니다")
	public ApiTemplate<ChallengeCertificationCreateResponseDto> createCertification(
		@PathVariable Long challengeId,
		@Valid @RequestBody ChallengeCertificationCreateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		ChallengeCertificationCreateResponseDto response = challengeCertificationService.createCertification(
			challengeId, request, currentUser
		);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.CERTIFICATION_CREATED, response);
	}

	@GetMapping("/my/challenges/certifications/personal")
	@AuthenticatedApi(reason = "개인 챌린지 인증 목록 조회는 로그인한 사용자만 가능합니다")
	public ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getPersonalChallengeCertifications(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		CursorTemplate<Long, ChallengeCertificationListResponseDto> response =
			challengeCertificationService.getPersonalChallengeCertifications(cursor, currentUser);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.PERSONAL_LIST_FOUND, response);
	}

	@GetMapping("/my/challenges/certifications/team")
	@AuthenticatedApi(reason = "팀 챌린지 인증 목록 조회는 로그인한 사용자만 가능합니다")
	public ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getTeamChallengeCertifications(
		@RequestParam(required = false) Long cursor,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		CursorTemplate<Long, ChallengeCertificationListResponseDto> response =
			challengeCertificationService.getTeamChallengeCertifications(cursor, currentUser);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.TEAM_LIST_FOUND, response);
	}

	@GetMapping("/my/challenges/certifications/{certId}")
	@AuthenticatedApi(reason = "챌린지 인증 상세 조회는 로그인한 사용자만 가능합니다")
	public ApiTemplate<ChallengeCertificationDetailResponseDto> getChallengeCertificationDetail(
		@PathVariable Long certId,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		ChallengeCertificationDetailResponseDto response =
			challengeCertificationService.getChallengeCertificationDetail(certId, currentUser);
		return ApiTemplate.ok(ChallengeCertificationResponseMessage.DETAIL_FOUND, response);
	}
}
*/
