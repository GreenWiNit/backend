package com.example.green.global.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * 관리자 권한이 필요한 API를 명시하는 메타 어노테이션입니다.
 *
 * 사용 예시:
 * {@code
 * @AdminApi(reason = "게시글 삭제는 관리자만 가능합니다")
 * @DeleteMapping("/api/posts/{id}")
 * public void deletePost(@PathVariable Long id) {
 *     // 게시글 삭제 로직
 * }
 * }
 *
 * - ADMIN 권한을 가진 사용자만 API를 호출할 수 있습니다.
 * - 권한 검증 로직이 자동으로 적용됩니다.
 * - 코드에서 관리자 전용 기능임을 명확히 보여 줍니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@PreAuthorize("hasRole('ADMIN')")
@ApiResponses(value = {
	@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (토큰 없음/유효하지 않음)",
		content = @Content(examples = @ExampleObject(value = "{\"success\":false,\"message\":\"JWT 토큰 유효성 검증에 실패했습니다.\"}"))),
	@ApiResponse(responseCode = "403", description = "관리자 권한이 없는 사용자",
		content = @Content(examples = @ExampleObject(value = "{\"success\":false,\"message\":\"접근이 거부되었습니다.\"}")))
})
public @interface AdminApi {

	String DEFAULT_REASON = "관리자 권한 필요";

	/**
	 * 관리자 권한이 필요한 사유를 명시 (문서화 목적)
	 */
	String reason() default DEFAULT_REASON;
}
