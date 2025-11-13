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
	summary = "사용자 아이템 장착 여부 변경",
	description = "사용자가 선택한 식물 아이템의 장착 여부를 변경합니다."
)
@ApiErrorStandard
@ApiResponse(
	responseCode = "200",
	description = "아이템 장착 여부 변경 성공",
	useReturnTypeSchema = true
)
@ApiResponse(
	responseCode = "404",
	description = "아이템을 찾을 수 없음",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
)
@ApiResponse(
	responseCode = "409",
	description = "동시 수정으로 인해 재시도가 필요함",
	content = @Content(schema = @Schema(implementation = ExceptionResponse.class))
)
public @interface GrowthApplicabilityDocs {
}
