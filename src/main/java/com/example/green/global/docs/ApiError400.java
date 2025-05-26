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
 * 공통 오류 응답(400)을 분리한 Swagger API 문서용 커스텀 어노테이션입니다.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
	@ApiResponse(
		responseCode = "400",
		description = "잘못된 요청 (입력값 검증 실패, 타입 오류 등)",
		content = @Content(
			mediaType = "application/json",
			schema = @Schema(implementation = ExceptionResponse.class),
			examples = {
				@ExampleObject(
					name = "ValidationError_MissingTitle",
					summary = "유효성 검증 실패 예시 - 제목 미기입",
					value = """
						{
							"success": false,
							"message": "제목은 필수 입력값입니다."
						}
						"""),
				@ExampleObject(
					name = "TypeMismatch_IdShouldBeNumber",
					summary = "타입 오류 - 숫자 필드에 문자 입력",
					value = """
							{
								"success": false,
								"message": "해당 필드는 숫자여야 합니다."
							}
						""")
			}
		))
})
public @interface ApiError400 {

	String summary() default "";

	String description() default "";
}
