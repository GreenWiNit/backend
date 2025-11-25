package com.example.green.domain.dashboard.growth.controller.docs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.green.global.docs.ApiErrorStandard;
import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
	summary = "사용자 식물 성장 데이터 및 아이템 조회",
	description = "로그인한 사용자의 식물 성장 데이터와 포인트 상점에서 교환한 아이템 전체 목록을 조회합니다."
)
@ApiErrorStandard
@ApiResponse(
	responseCode = "200",
	description = "성장 데이터 및 아이템 조회 성공",
	useReturnTypeSchema = true
)
@ApiResponse(
	responseCode = "404",
	description = "사용자의 성장 데이터 또는 아이템이 존재하지 않습니다",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
)
public @interface GrowthCreateDocs {
}


