package com.example.green.global.docs;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.green.global.error.dto.ExceptionResponse;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 공통 오류 응답(400, 401, 404, 500)을 포함하는 Swagger API 문서용 커스텀 어노테이션입니다.
 * 성공 응답과 개별 에러 응답만 개별 메서드에 @ApiResponse 로 명시하고, 공통 에러는 해당 어노테이션으로 처리합니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
	@ApiResponse(
		responseCode = "401",
		description = "인증되지 않은 사용자 (토큰 누락 또는 만료)",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ExceptionResponse.class),
			examples = @ExampleObject(
				name = "UnauthorizedError",
				summary = "인증 실패 예시",
				value = """
					{
					  "success": false,
					  "message": "인증 정보가 없습니다. 다시 로그인해주세요."
					}
				"""
			)
		)
	),
	@ApiResponse(
		responseCode = "404",
		description = "리소스를 찾을 수 없습니다",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ExceptionResponse.class),
			examples = @ExampleObject(
				name = "NotFoundError",
				summary = "존재하지 않는 리소스 예시",
				value = """
					{
					  "success": false,
					  "message": "요청하신 페이지를 찾을 수 없습니다."
					}
				"""
			)
		)
	),
	@ApiResponse(
		responseCode = "500",
		description = "서버 내부 오류",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ExceptionResponse.class),
			examples = @ExampleObject(
				name = "ServerError",
				summary = "알 수 없는 서버 오류 예시",
				value = """
					{
					  "success": false,
					  "message": "알 수 없는 서버 에러가 발생했습니다."
					}
				"""
			)
		)
	)
})
public @interface ApiErrorStandard {

	String summary() default "";
	String description() default "";
}
