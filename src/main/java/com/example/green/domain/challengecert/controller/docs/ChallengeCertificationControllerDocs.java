package com.example.green.domain.challengecert.controller.docs;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationCreateResponseDto;
import com.example.green.global.api.ApiTemplate;
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

@Tag(name = "챌린지 인증 API", description = "챌린지 인증 생성 API")
public interface ChallengeCertificationControllerDocs {

	@Operation(
		summary = "챌린지 인증 생성",
		description = """
			챌린지 인증을 생성합니다.
			- 참여중인 챌린지에 대해서만 인증 가능합니다.
			- 하루에 한 번만 인증 가능합니다 (선택한 날짜 기준).
			- 인증 날짜는 미래 날짜만 선택할 수 없습니다.
			- 인증 후기는 최대 45자까지 입력 가능합니다.
			"""
	)
	@ApiResponse(responseCode = "200", description = "챌린지 인증 생성 성공", useReturnTypeSchema = true)
	@ApiResponse(
		responseCode = "400",
		description = """
			1. 이미 해당 날짜에 인증이 존재합니다.
			2. 참여하지 않은 챌린지입니다.
			3. 인증 날짜는 미래 날짜를 선택할 수 없습니다.
			4. 입력값이 유효하지 않습니다.
			""",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiResponse(
		responseCode = "404",
		description = "챌린지를 찾을 수 없습니다.",
		content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
	)
	@ApiError400
	@ApiErrorStandard
	ApiTemplate<ChallengeCertificationCreateResponseDto> createCertification(
		@Parameter(
			name = "challengeId",
			description = "챌린지 ID",
			in = ParameterIn.PATH,
			required = true,
			example = "1"
		)
		@PathVariable Long challengeId,

		@Parameter(description = "챌린지 인증 생성 요청 정보", required = true)
		@Valid @RequestBody ChallengeCertificationCreateRequestDto request,

		@Parameter(hidden = true)
		@AuthenticationPrincipal PrincipalDetails currentUser
	);
} 