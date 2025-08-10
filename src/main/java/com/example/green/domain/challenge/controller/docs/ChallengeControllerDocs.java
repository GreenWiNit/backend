package com.example.green.domain.challenge.controller.docs;

import com.example.green.global.api.NoContent;
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

	NoContent joinChallenge(Long chlgNo);

	@Operation(summary = "팀 챌린지 참여", description = "팀 챌린지에 참여합니다.")
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
	NoContent joinTeamChallenge(
		@Parameter(name = "chlgNo", description = "참여할 팀 챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long chlgNo, PrincipalDetails currentUser
	);

	@Operation(summary = "개인 챌린지 참여", description = "개인 챌린지에 참여합니다.")
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
	NoContent joinPersonalChallenge(
		@Parameter(name = "chlgNo", description = "참여할 개인 챌린지 ID",
			in = ParameterIn.PATH, required = true, example = "1") Long chlgNo, PrincipalDetails currentUser
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
