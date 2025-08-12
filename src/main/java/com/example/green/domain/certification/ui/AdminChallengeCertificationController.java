package com.example.green.domain.certification.ui;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.certification.application.ChallengeCertificationService;
import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.infra.filter.ChallengeCertificationFilter;
import com.example.green.domain.certification.ui.docs.AdminChallengeCertificationControllerDocs;
import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.domain.certification.ui.dto.CertificationVerifyDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.security.annotation.AdminApi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/certifications/challenges")
@AdminApi
public class AdminChallengeCertificationController implements AdminChallengeCertificationControllerDocs {

	private final ChallengeCertificationService challengeCertificationService;
	private final ChallengeCertificationQuery challengeCertificationQuery;

	@GetMapping
	public ApiTemplate<PageTemplate<AdminCertificateSearchDto>> getCertifications(
		@Valid @ParameterObject @ModelAttribute ChallengeCertificationFilter filter
	) {
		PageTemplate<AdminCertificateSearchDto> result = challengeCertificationQuery.search(filter);
		return ApiTemplate.ok(CertificationResponseMessage.CERTIFICATIONS_READ_SUCCESS, result);
	}

	@PatchMapping("/approve")
	public NoContent approveCertification(@RequestBody @Valid CertificationVerifyDto dto) {
		challengeCertificationService.approve(dto.certificationIds());
		return NoContent.ok(CertificationResponseMessage.CERTIFICATIONS_APPROVE_SUCCESS);
	}
}
