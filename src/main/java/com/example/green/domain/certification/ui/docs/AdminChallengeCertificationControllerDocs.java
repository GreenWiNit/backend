package com.example.green.domain.certification.ui.docs;

import com.example.green.domain.certification.infra.filter.ChallengeCertificationFilter;
import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.docs.ApiErrorStandard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[챌린지 인증-관리자] 챌린지 인증 API", description = "챌린지 인증 관련 API")
public interface AdminChallengeCertificationControllerDocs {

	@Operation(summary = "챌린지 인증 목록 조회 (ad_B01_003, ad_B01_006)", description = "챌린지 인증 확인")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 인증 목록 조회에 성공헀습니다.")
	ApiTemplate<PageTemplate<AdminCertificateSearchDto>> getCertifications(ChallengeCertificationFilter filter);
}
