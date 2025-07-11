package com.example.green.domain.auth.constants;

/**
 * 인증 관련 공통 상수 정의
 */
public final class AuthConstants {

	// 공통 토큰 타입
	public static final String TOKEN_TYPE_ACCESS = "access";
	public static final String TOKEN_TYPE_REFRESH = "refresh";
	public static final String TOKEN_TYPE_TEMP = "temp";
	// 공통 쿠키 이름
	public static final String REFRESH_TOKEN_COOKIE_NAME = "TokenManager";
	// 공통 Role
	public static final String ROLE_USER = "ROLE_USER";

	private AuthConstants() {
	}
}
