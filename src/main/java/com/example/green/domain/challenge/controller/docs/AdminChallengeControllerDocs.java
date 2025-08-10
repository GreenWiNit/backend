package com.example.green.domain.challenge.controller.docs;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.*;

import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeCreateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeImageUpdateRequestDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeUpdateRequestDto;
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

	@Operation(
		summary = "챌린지 이미지 추가/수정",
		description = "챌린지의 이미지를 URL로 등록/수정합니다. (어드민 전용)"
	)
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 이미지 변경 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	@ApiResponse(responseCode = "404", description = "챌린지 없음",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<AdminChallengeDetailDto> updateChallengeImage(
		@Parameter(
			name = "challengeId",
			description = "챌린지 ID",
			in = PATH, required = true, example = "1") Long challengeId,
		@RequestBody(description = "챌린지 이미지 URL", required = true, content = @Content(schema = @Schema(
			implementation = AdminChallengeImageUpdateRequestDto.class))) AdminChallengeImageUpdateRequestDto request);

	@Operation(summary = "팀 챌린지 생성", description = "챌린지를 생성합니다. (이미지 URL 포함)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Long> createTeamChallenge(
		@RequestBody(description = "챌린지 생성 요청", required = true, content = @Content(schema =
		@Schema(implementation = AdminChallengeCreateRequestDto.class))) AdminChallengeCreateRequestDto request
	);

	@Operation(summary = "개인 챌린지 생성", description = "개인 챌린지를 생성합니다. (이미지 URL 포함)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Long> createPersonalChallenge(
		@RequestBody(description = "챌린지 생성 요청", required = true, content = @Content(schema =
		@Schema(implementation = AdminChallengeCreateRequestDto.class))) AdminChallengeCreateRequestDto request
	);

	@Operation(summary = "챌린지 수정 (사진x)",
		description = "챌린지를 수정합니다. (이미지 및 전시 상태 제외)")
	@ApiErrorStandard
	@ApiResponse(responseCode = "200", description = "챌린지 수정 성공", useReturnTypeSchema = true)
	@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
	ApiTemplate<Void> updateChallenge(
		@Parameter(name = "challengeId", description = "챌린지 ID",
			in = PATH, required = true, example = "1") Long challengeId,
		@RequestBody(description = "챌린지 수정 요청", required = true, content = @Content(schema = @Schema(
			implementation = AdminChallengeUpdateRequestDto.class))) AdminChallengeUpdateRequestDto request);

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
