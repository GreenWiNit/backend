package com.example.green.domain.dashboard.rankingmodule.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.green.domain.dashboard.rankingmodule.dto.LoadWeeklyRankingResponse;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.DetailedExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "대시보드 랭킹 API", description = "상위 8명의 주간 단위 랭킹과 로그인 사용자의 주간 랭킹을 조회합니다.")
public interface WeeklyRankingControllerDocs {

	@Operation(
		summary = "대시보드 주간 랭킹 조회",
		description = """
			주어진 주차의 시작 날짜(`weekStart`)를 기준으로 
			- 상위 8명의 랭킹 데이터
			- 로그인한 사용자의 랭킹 데이터  
			를 함께 조회합니다.
			"""
	)
	@ApiErrorStandard
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "주간 랭킹 조회 성공",
			useReturnTypeSchema = true
		),
		@ApiResponse(
			responseCode = "404",
			description = "해당 주차에 사용자의 랭킹 데이터가 존재하지 않는 경우",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = DetailedExceptionResponse.class),
				examples = @ExampleObject(
					name = "NotFoundUser",
					summary = "랭킹 데이터가 존재하지 않는 경우",
					value = """
							{
								"success": false,
								"message": "사용자를 찾을 수 없습니다."
							}
						"""
				)
			)
		)
	})
	ApiTemplate<LoadWeeklyRankingResponse> getWeeklyRanking(
		@Schema(
			description = "조회할 주차의 시작 날짜 (예: 2025-10-27)",
			example = "2025-10-27",
			requiredMode = Schema.RequiredMode.REQUIRED
		)
		@RequestParam("weekStart")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart
	);

}
