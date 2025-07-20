package com.example.green.domain.challengecert.controller.docs;

import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationDetailResponseDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.docs.ApiError400;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;
import com.example.green.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "챌린지 인증 API", description = "챌린지 인증 등록, 조회 API")
public interface ChallengeCertificationControllerDocs {

	@Operation(
		summary = "챌린지 인증 등록",
		description = """
			사용자가 특정 챌린지에 대한 인증을 등록합니다.
			- 하루에 한 번만 인증 가능
			- 미래 날짜로는 인증 불가
			- 챌린지에 참여 중인 사용자만 인증 가능
			"""
	)
	@ApiResponse(responseCode = "200", description = "인증 등록 성공", useReturnTypeSchema = true)
	@ApiError400
	@ApiErrorStandard
	ApiTemplate<ChallengeCertificationCreateResponseDto> createCertification(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "인증 등록 요청", required = true)
		@Valid ChallengeCertificationCreateRequestDto request,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(
		summary = "개인 챌린지 인증 목록 조회",
		description = """
			현재 사용자의 개인 챌린지 인증 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 기본 20개씩 조회됩니다.
			첫 페이지 조회 시에는 cursor 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "개인 챌린지 인증 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getPersonalChallengeCertifications(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 인증 ID (선택, 첫 페이지 조회 시 생략)")
		Long cursor,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(
		summary = "팀 챌린지 인증 목록 조회",
		description = """
			현재 사용자의 팀 챌린지 인증 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 기본 20개씩 조회됩니다.
			첫 페이지 조회 시에는 cursor 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "팀 챌린지 인증 목록 조회 성공", useReturnTypeSchema = true)
	@ApiErrorStandard
	ApiTemplate<CursorTemplate<Long, ChallengeCertificationListResponseDto>> getTeamChallengeCertifications(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 인증 ID (선택, 첫 페이지 조회 시 생략)")
		Long cursor,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(
		summary = "챌린지 인증 상세 조회",
		description = """
			특정 인증의 상세 정보를 조회합니다.
			본인의 인증만 조회 가능합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "챌린지 인증 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(
		responseCode = "404",
		description = "인증을 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiErrorStandard
	ApiTemplate<ChallengeCertificationDetailResponseDto> getChallengeCertificationDetail(
		@Parameter(name = "certId", description = "인증 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long certId,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);
}
