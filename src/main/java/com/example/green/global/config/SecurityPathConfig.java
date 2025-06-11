package com.example.green.global.config;

import org.springframework.util.AntPathMatcher;
import java.util.Arrays;

public class SecurityPathConfig {
	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	// GET 요청만 가능한 아이들 (인증 없이 조회 가능)
	public static final String[] PUBLIC_GET_PATHS = {
		// TODO: API 경로가 정해지면 추가
		"/api/health",
		"/api/members/profile/**",  // 회원 프로필 조회
	};

	// 인증이 필요없는 정적 리소스 및 공개 경로
	public static final String[] PUBLIC_STATIC_PATHS = {
		// 개발용 (TODO: 프로덕션에서는 제거)
		"/h2-console/**",
		
		// OAuth2 관련
		"/login",
		"/oauth2/authorization/**",
		"/login/oauth2/code/**",
		
		// Swagger 관련
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-ui.html",
		"/swagger-resources/**",
		"/webjars/**",
		
		// 정적 리소스
		"/static/**",
		"/public/**",
		"/resources/**",
		"/META-INF/resources/**",
		"/favicon.ico",
		
		// Health Check
		"/actuator/health",
		"/v1/health-check",
	};

	// 사용자 API 관련 설정
	public static final String USER_API_PATH = "/api/members/**";

	// Green 프로젝트 관련 API
	public static final String GREEN_API_PATH = "/api/green/**";

	
	public static boolean isPermitAllPath(String path) {
		return Arrays.stream(PUBLIC_STATIC_PATHS)
			.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	// 사용자 API 관련 설정
	public static boolean isUserApiPath(String path, String method) {
		return pathMatcher.match(USER_API_PATH, path) && 
			(method.equals("GET") || method.equals("POST"));
	}

	// Green API 관련 설정
	public static boolean isGreenApiPath(String path, String method) {
		return pathMatcher.match(GREEN_API_PATH, path) && 
			(method.equals("GET"));
	}


	// GET 요청만 인증없이 통과
	public static boolean isPublicGetPath(String path, String method) {
		return "GET".equalsIgnoreCase(method) &&
				Arrays.stream(PUBLIC_GET_PATHS)
						.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}

	public static boolean shouldSkipFilter(String path, String method) {
		return isPermitAllPath(path) || 
			   isUserApiPath(path, method) || 
			   isGreenApiPath(path, method) || 
			   isPublicGetPath(path, method)
				;
	}
} 