package com.example.green.domain.challenge.controller.query.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeParticipantListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengesDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "[챌린지-관리자] 팀 챌린지 관리 API", description = "관리자 팀 챌린지 생성/수정/이미지/전시여부 등 관리 API")
public interface AdminTeamChallengeQueryControllerDocs {

	@Operation(summary = "팀 챌린지 목록 조회", description = "팀 챌린지 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "팀 챌린지 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<PageTemplate<AdminTeamChallengesDto>> getTeamChallenges(
		@Parameter(description = "페이지 수 (생략가능)") Integer page,
		@Parameter(description = "페이지 사이즈(생략 가능)", example = "10") Integer size
	);

	@Operation(summary = "팀 챌린지 목록 엑셀 다운로드", description = "팀 챌린지 목록 엑셀 다운로드")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200")
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	void downloadTeamChallenges(HttpServletResponse response);

	@Operation(summary = "팀 챌린지 상세 조회", description = "팀 챌린지 상세 정보를 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 상세 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminChallengeDetailDto> getTeamChallengeDetail(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId);

	@Operation(summary = "챌린지 참여자 목록 조회", description = "챌린지의 참여자 목록을 조회합니다. (10개씩 조회)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "참여자 목록 조회 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<CursorTemplate<Long, AdminChallengeParticipantListResponseDto>> getChallengeParticipants(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = PATH, required = true, example = "1") Long challengeId,
		@Parameter(description = "커서 (마지막 참여자 ID) - 첫 번째 조회 시에는 아무것도 넣지 말고 조회하세요") Long cursor
	);
}
