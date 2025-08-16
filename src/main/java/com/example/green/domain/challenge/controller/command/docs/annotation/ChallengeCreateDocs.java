package com.example.green.domain.challenge.controller.command.docs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.green.domain.challenge.controller.command.dto.AdminChallengeCreateDto;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
	summary = "챌린지 생성 (ad_B01_009, ad_B01_010)",
	description = "챌린지를 생성합니다. (이미지 URL 포함)",
	requestBody = @RequestBody(
		description = """
			챌린지 생성 요청 정보
			
			**주요 필드 설명:**
			- displayStatus: 디스플레이 여부
			  - HIDDEN: 숨김 처리
			  - VISIBLE: 화면에 표시
			""",
		required = true,
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = AdminChallengeCreateDto.class)
		)
	)
)
@ApiErrorStandard
@ApiResponse(responseCode = "200", description = "챌린지 생성 성공", useReturnTypeSchema = true)
@ApiResponse(responseCode = "403", description = "관리자 권한이 필요합니다.",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
@Inherited
public @interface ChallengeCreateDocs {
}
