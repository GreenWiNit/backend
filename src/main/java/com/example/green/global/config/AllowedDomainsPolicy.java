package com.example.green.global.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 허용된 도메인 정책을 중앙 관리하는 클래스
 * CORS 설정과 OAuth2 리다이렉트 검증에서 공통으로 사용
 */
@Slf4j
@Component
public class AllowedDomainsPolicy {

	// 정적 허용 도메인 목록
	public static final List<String> STATIC_ALLOWED_ORIGINS = Arrays.asList(
		"https://greenwinit-admin-panel.greenwinit01.workers.dev",
		"https://greenwinit.pages.dev",
		"https://greenwinit-admin-panel.pages.dev",
		"http://localhost:5173",
		"http://localhost:5174", 
		"http://localhost:3000"
	);

	// 와일드카드 패턴 도메인 목록
	public static final List<String> ALLOWED_ORIGIN_PATTERNS = Arrays.asList(
		"https://*.greenwinit01.workers.dev",
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
		
		log.info("AllowedDomainsPolicy 초기화 - frontendBaseUrl: {}, backendBaseUrl: {}", 
			frontendBaseUrl, backendBaseUrl);
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
		List<String> allAllowedOrigins = getAllAllowedOrigins();
		
		log.debug("도메인 검증 시작 - 대상 URL: {}", url);
		log.debug("허용된 정확한 도메인 목록: {}", allAllowedOrigins);
		log.debug("허용된 패턴 목록: {}", ALLOWED_ORIGIN_PATTERNS);

		// 정확한 매치 확인
		if (allAllowedOrigins.contains(url)) {
			log.info("도메인 검증 성공 (정확한 매치) - URL: {}", url);
			return true;
		}

		// 패턴 매치 확인
		for (String pattern : ALLOWED_ORIGIN_PATTERNS) {
			if (matchesPattern(url, pattern)) {
				log.info("도메인 검증 성공 (패턴 매치) - URL: {}, 매치된 패턴: {}", url, pattern);
				return true;
			}
		}

		log.warn("도메인 검증 실패 - URL: {}, 허용된 도메인/패턴에 해당하지 않음", url);
		return false;
	}

	/**
	 * 와일드카드 패턴과 URL 매칭
	 */
	private boolean matchesPattern(String url, String pattern) {
		if (!pattern.contains("*")) {
			return url.equals(pattern);
		}
		
		String regex = pattern.replace("*", "[^./]+");
		boolean matches = url.matches(regex);
		log.debug("패턴 매칭 - URL: {}, 패턴: {}, 정규식: {}, 결과: {}", url, pattern, regex, matches);
		return matches;
	}
} 