package com.example.green.domain.challenge.controller.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "관리자 챌린지 관리 API", description = "관리자 챌린지 생성/수정/이미지/전시여부 등 관리 API")
public interface AdminChallengeControllerDocs {

	@Operation(summary = "팀 챌린지 생성", description = "챌린지를 생성합니다. (이미지 URL 포함)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Long> createTeamChallenge(
		@RequestBody(description = "챌린지 생성 요청", required = true, content = @Content(schema =
		@Schema(implementation = AdminChallengeCreateDto.class))) AdminChallengeCreateDto request
	);

	@Operation(summary = "개인 챌린지 생성", description = "개인 챌린지를 생성합니다. (이미지 URL 포함)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Long> createPersonalChallenge(
		@RequestBody(description = "챌린지 생성 요청", required = true, content = @Content(schema =
		@Schema(implementation = AdminChallengeCreateDto.class))) AdminChallengeCreateDto request
	);

	@Operation(summary = "개인 챌린지 수정", description = "개인 챌린지를 수정합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 수정 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Void> updatePersonalChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId,
		@RequestBody(
			description = "챌린지 수정 요청", required = true,
			content = @Content(schema = @Schema(implementation = AdminChallengeUpdateDto.class))
		) AdminChallengeUpdateDto request);

	@Operation(summary = "팀 챌린지 수정", description = "팀 챌린지를 수정합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 수정 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Void> updateTeamChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId,
		@RequestBody(
			description = "챌린지 수정 요청", required = true,
			content = @Content(schema = @Schema(implementation = AdminChallengeUpdateDto.class))
		) AdminChallengeUpdateDto request);

	@Operation(summary = "팀 챌린지 전시", description = "팀 챌린지를 전시 상태로 변경합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "전시 상태 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent showTeamChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId
	);

	@Operation(summary = "팀 챌린지 미전시", description = "팀 챌린지를 미전시 상태로 변경합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "전시 상태 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent hideTeamChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId
	);

	@Operation(summary = "개인 챌린지 전시", description = "개인 챌린지를 전시 상태로 변경합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "전시 상태 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent showPersonalChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId
	);

	@Operation(summary = "개인 챌린지 미전시", description = "개인 챌린지를 미전시 상태로 변경합니다.")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "전시 상태 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	NoContent hidePersonalChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID", in = PATH, required = true, example = "1")
		Long challengeId
	);
}
