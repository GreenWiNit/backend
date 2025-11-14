package com.example.green.domain.dashboard.rankingmodule.controller.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
	summary = "내 주간 랭킹 데이터 조회",
	description = """
		로그인한 사용자가 특정 주차(weekStart)에 쌓은
		- 주간 포인트(totalPoint)
		- 주간 챌린지 인증 횟수(certificationCount)
		를 조회합니다.
		
		만약 해당 주차에 활동한 기록이 없으면
		포인트와 챌린지 수는 0으로 반환됩니다.
		""",
	parameters = {
		@Parameter(
			name = "weekStart",
			description = "주차 시작 날짜 (yyyy-MM-dd 형식)",
			example = "2025-02-01",
			required = true
		)
	}
)
@ApiErrorStandard
@ApiResponse(responseCode = "200", description = "내 주간 랭킹 데이터 조회 성공", useReturnTypeSchema = true)
@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
public @interface WeeklyRankingMyDataCreateDocs {
}
