package com.example.green.domain.dashboard.rankingmodule.controller;

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
	summary = "대시보드 주간 랭킹 조회",
	description = """
		주어진 주차의 시작 날짜(weekStart)를 기준으로
		- 상위 8명의 랭킹 데이터
		- 로그인한 사용자의 랭킹 데이터를 조회합니다.
		""",
	parameters = {
		@Parameter(
			name = "weekStart",
			description = "주차 시작 날짜 (형식: yyyy-MM-dd, 예: 2025-02-01)",
			example = "2025-02-01",
			required = true
		)
	}
)
@ApiErrorStandard
@ApiResponse(responseCode = "200", description = "대시보드 주간 랭킹 조회 성공", useReturnTypeSchema = true)
@ApiResponse(responseCode = "404", description = "로그인한 사용자의 랭킹 데이터가 없습니다",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
public @interface WeeklyRankingCreateDocs {
}
