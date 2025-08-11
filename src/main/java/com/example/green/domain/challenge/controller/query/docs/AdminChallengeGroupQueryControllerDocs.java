package com.example.green.domain.challenge.controller.query.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeGroupDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[챌린지-관리자] 팀 챌린지 그룹 관리 API", description = "관리자 팀 챌린지 그룹 관리 API")
public interface AdminChallengeGroupQueryControllerDocs {

	@Operation(summary = "그룹 목록 조회 (ad_B01-007, 팀 목록)", description = "챌린지 그룹 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "그룹 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<PageTemplate<AdminChallengeGroupDto>> getGroups(
		@Parameter(description = "페이지 수 (생략가능)") Integer page,
		@Parameter(description = "페이지 사이즈(생략 가능)", example = "10") Integer size
	);

	@Operation(summary = "그룹 상세 조회 (ad_B01-007, 팀 상세 정보", description = "챌린지 그룹 상세 정보를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "그룹 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminChallengeGroupDetailDto> getGroupDetail(
		@Parameter(description = "그룹 ID", in = PATH, required = true, example = "1") Long groupId
	);

	@Operation(
		summary = "팀 챌린지 참여자 목록 조회 (ad_B01_005), 참여자 정보",
		description = "챌린지의 참여자 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "참여자 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<PageTemplate<AdminTeamParticipantDto>> getChallengeParticipant(
		@Parameter(description = "챌린지 ID", in = PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "페이지 수 (생략가능)") Integer page,
		@Parameter(description = "페이지 사이즈(생략 가능)", example = "10") Integer size
	);
}
