package com.example.green.global.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 허용된 도메인 정책을 중앙 관리하는 클래스
 * CORS 설정과 OAuth2 리다이렉트 검증에서 공통으로 사용
 */
@Component
public class AllowedDomainsPolicy {

	// 정적 허용 도메인 목록
	public static final List<String> STATIC_ALLOWED_ORIGINS = Arrays.asList(
		"https://greenwinit.pages.dev",
		"https://greenwinit-admin-panel.pages.dev",
		"http://localhost:5173",
		"http://localhost:5174", 
		"http://localhost:3000"
	);

	// 와일드카드 패턴 도메인 목록
	public static final List<String> ALLOWED_ORIGIN_PATTERNS = Arrays.asList(
		"https://*.greenwinit.pages.dev",
		"https://*.greenwinit-admin-panel.pages.dev",
		"https://*.greenwinit.store"
	);

	private final String frontendBaseUrl;
	private final String backendBaseUrl;

	public AllowedDomainsPolicy(
		@Value("${app.frontend.base-url}") String frontendBaseUrl,
		@Value("${app.backend.base-url}") String backendBaseUrl) {
		this.frontendBaseUrl = frontendBaseUrl;
		this.backendBaseUrl = backendBaseUrl;
	}

	/**
	 * 환경변수를 포함한 모든 허용된 도메인 목록 반환
	 */
	public List<String> getAllAllowedOrigins() {
		List<String> allOrigins = new ArrayList<>(STATIC_ALLOWED_ORIGINS);
		allOrigins.add(frontendBaseUrl);
		allOrigins.add(backendBaseUrl);
		return allOrigins;
	}

	/**
	 * 와일드카드 패턴 목록 반환
	 */
	public List<String> getAllowedOriginPatterns() {
		return ALLOWED_ORIGIN_PATTERNS;
	}

	/**
	 * 주어진 URL이 허용된 도메인인지 확인
	 */
	public boolean isAllowedOrigin(String url) {
		// 정확한 매치 확인
		if (getAllAllowedOrigins().contains(url)) {
			return true;
		}

		// 패턴 매치 확인
		return ALLOWED_ORIGIN_PATTERNS.stream()
			.anyMatch(pattern -> matchesPattern(url, pattern));
	}

	/**
	 * 와일드카드 패턴과 URL 매칭
	 */
	private boolean matchesPattern(String url, String pattern) {
		if (!pattern.contains("*")) {
			return url.equals(pattern);
		}
		
		String regex = pattern.replace("*", "[^./]+");
		return url.matches(regex);
	}
} 