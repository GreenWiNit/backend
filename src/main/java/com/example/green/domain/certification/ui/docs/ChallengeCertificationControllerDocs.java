package com.example.green.domain.certification.ui.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.domain.certification.ui.dto.PersonalChallengeCertificateDto;
import com.example.green.domain.certification.ui.dto.TeamChallengeCertificateDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[챌린지 인증-클라이언트] 챌린지 인증 API", description = "챌린지 인증 관련 API")
public interface ChallengeCertificationControllerDocs {

	@Operation(summary = "개인 챌린지 인증 (B01_004)", description = "챌린지 인증")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "개인 챌린지 인증 성공")
	NoContent certificateTeamChallenge(
		@Parameter(description = "챌린지 ID", in = PATH, required = true, example = "1") Long challengeId,
		PersonalChallengeCertificateDto dto,
		PrincipalDetails principalDetails
	);

	@Operation(summary = "팀 챌린지 인증 (B01_006)", description = "팀 챌린지 인증")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 챌린지 인증 성공")
	NoContent certificateTeamChallenge(
		@Parameter(description = "그룹 ID", in = PATH, required = true, example = "1") Long groupId,
		TeamChallengeCertificateDto dto,
		PrincipalDetails principalDetails
	);

	@Operation(summary = "팀 챌린지 인증 목록 조회 (G01_003)", description = "팀 챌린지 인증 목록 조회")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 챌린지 인증 목록 조회에 성공")
	ApiTemplate<CursorTemplate<String, ChallengeCertificationDto>> getPersonalCertifications(
		@Parameter(description = "조회 타입 (팀: T, P: 개인 - Default)", example = "P") String type,
		@Parameter(description = "다음 페이지 조회를 위한 마지막 커서 (선택, 첫 페이지 조회 시 생략)", example = "1") String cursor,
		@Parameter(description = "페이지 사이즈(생략 가능)") Integer size,
		PrincipalDetails principalDetails
	);
}
