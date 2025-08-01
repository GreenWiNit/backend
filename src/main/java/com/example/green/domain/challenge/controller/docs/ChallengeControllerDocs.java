package com.example.green.domain.challenge.controller.docs;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailResponseDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.api.page.CursorTemplate;
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

@Tag(name = "챌린지 API", description = "챌린지 조회, 참여, 탈퇴 API")
public interface ChallengeControllerDocs {

	@Operation(
		summary = "개인 챌린지 목록 조회",
		description = """
			현재 진행 중인 개인 챌린지 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 있어 다음 페이지 조회 시 이전 응답의 마지막 챌린지 ID를 커서로 사용합니다.
			첫 페이지 조회 시에는 커서 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "개인 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getPersonalChallenges(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 챌린지 ID (선택, 첫 페이지 조회 시 생략)", example = "1") Long cursor
	);

	@Operation(
		summary = "팀 챌린지 목록 조회",
		description = """
			현재 진행 중인 팀 챌린지 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 있어 다음 페이지 조회 시 이전 응답의 마지막 챌린지 ID를 커서로 사용합니다.
			첫 페이지 조회 시에는 커서 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getTeamChallenges(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 챌린지 ID (선택, 첫 페이지 조회 시 생략)", example = "1") Long cursor
	);

	@Operation(
		summary = "챌린지 상세 조회",
		description = """
			챌린지 ID로 상세 정보를 조회합니다.
			- 비로그인 상태: 참여하기 버튼 표시
			- 로그인 상태 & 미참여: 참여하기 버튼 표시
			- 로그인 상태 & 참여 중: 참여하기 버튼 미표시
			"""
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(
		responseCode = "404",
		description = "챌린지를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	ApiTemplate<ChallengeDetailResponseDto> getChallengeDetail(
		@Parameter(name = "chlgNo", description = "조회할 챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long chlgNo,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(
		summary = "내 개인 챌린지 목록 조회",
		description = """
			내가 참여 중인 개인 챌린지 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 있어 다음 페이지 조회 시 이전 응답의 마지막 챌린지 ID를 커서로 사용합니다.
			첫 페이지 조회 시에는 커서 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "내 개인 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyPersonalChallenges(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 챌린지 ID (선택, 첫 페이지 조회 시 생략)", example = "1") Long cursor,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(
		summary = "내 팀 챌린지 목록 조회",
		description = """
			내가 참여 중인 팀 챌린지 목록을 조회합니다.
			커서 기반 페이지네이션이 적용되어 있어 다음 페이지 조회 시 이전 응답의 마지막 챌린지 ID를 커서로 사용합니다.
			첫 페이지 조회 시에는 커서 값을 전달하지 않아도 됩니다.
			"""
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "내 팀 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	ApiTemplate<CursorTemplate<Long, ChallengeListResponseDto>> getMyTeamChallenges(
		@Parameter(description = "다음 페이지 조회를 위한 마지막 챌린지 ID (선택, 첫 페이지 조회 시 생략)", example = "1") Long cursor,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(summary = "챌린지 참여", description = "특정 챌린지에 참여합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 참여 성공", useReturnTypeSchema = true)
	@ApiResponse(
		responseCode = "400",
		description = """
			1. 이미 참여 중인 챌린지입니다.
			2. 챌린지 참여 기간이 아닙니다.
			""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404",
		description = "챌린지를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent joinChallenge(
		@Parameter(name = "chlgNo", description = "참여할 챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long chlgNo,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);

	@Operation(summary = "챌린지 탈퇴", description = "참여 중인 챌린지에서 탈퇴합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 탈퇴 성공", useReturnTypeSchema = true)
	@ApiResponse(
		responseCode = "400",
		description = """
			1. 참여하지 않은 챌린지입니다.
			2. 챌린지 탈퇴 기간이 아닙니다.
			""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404",
		description = "챌린지를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	NoContent leaveChallenge(
		@Parameter(name = "chlgNo", description = "탈퇴할 챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long chlgNo,
		@Parameter(hidden = true) PrincipalDetails currentUser
	);
}
