package com.example.green.domain.challengecert.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.challengecert.controller.docs.ChallengeCertificationControllerDocs;
import com.example.green.domain.challengecert.controller.message.ChallengeCertificationResponseMessage;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.service.ChallengeCertificationService;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeCertificationController implements ChallengeCertificationControllerDocs {

	private final ChallengeCertificationService challengeCertificationService;

	@Override
	@PostMapping("/{challengeId}/certifications")
	@AuthenticatedApi(reason = "챌린지 인증은 로그인한 사용자만 가능합니다")
	public ApiTemplate<ChallengeCertificationCreateResponseDto> createCertification(
		@PathVariable Long challengeId,
		@Valid @RequestBody ChallengeCertificationCreateRequestDto request,
		@AuthenticationPrincipal PrincipalDetails currentUser
	) {
		ChallengeCertificationCreateResponseDto response = challengeCertificationService.createCertification(
			challengeId,
			request,
			currentUser
		);

		return ApiTemplate.ok(
			ChallengeCertificationResponseMessage.CERTIFICATION_CREATED,
			response
		);
	}
} 