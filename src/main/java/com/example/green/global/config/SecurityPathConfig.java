package com.example.green.global.config;

import org.springframework.util.AntPathMatcher;
import java.util.Arrays;

/**
 * Spring Security 경로 설정을 중앙 집중 관리하는 클래스
 * - 모든 보안 관련 경로 설정을 한 곳에서 관리
 * - WebSecurityConfig에서 이 설정들을 참조하여 사용
 */
public final class SecurityPathConfig {
	private SecurityPathConfig() { /* 인스턴스화 방지 */ }

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	// ========== 공개 경로 설정 ==========

	// 인증이 필요없는 정적 리소스 및 공개 경로
	public static final String[] PUBLIC_STATIC_PATHS = {
			"/h2-console/**",
			"/login",
			"/oauth2/authorization/**",
			"/login/oauth2/code/**",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/swagger-ui.html",
			"/swagger-resources/**",
			"/webjars/**",
			"/favicon.ico",
			"/actuator/health",
			"/v1/health-check",
			"/api/health"
	};

	// GET 요청만 가능한 공개 조회 API
	public static final String[] PUBLIC_GET_PATHS = {
			"/api/members/profile/**"
	};

	// ========== API 경로 설정 ==========

	public static final String USER_API_PATH  = "/api/members/**";
	public static final String GREEN_API_PATH = "/api/green/**";
	public static final String DEV_API_PATH   = "/v1/**";

	// ========== 유틸리티 메서드들 ==========

	public static boolean isPublicStaticPath(String path) {
		return Arrays.stream(PUBLIC_STATIC_PATHS)
				.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	public static boolean isPublicGetPath(String path, String method) {
		return "GET".equalsIgnoreCase(method) &&
				Arrays.stream(PUBLIC_GET_PATHS)
						.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	public static boolean isDevApiPath(String path) {
		return pathMatcher.match(DEV_API_PATH, path);
	}
}