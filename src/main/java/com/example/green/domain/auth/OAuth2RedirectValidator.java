package com.example.green.domain.auth;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.example.green.domain.auth.resolver.CustomAuthorizationRequestResolver;
import com.example.green.global.config.AllowedDomainsPolicy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * OAuth2 리다이렉트 보안 검증 서비스
 */
@Slf4j
@Component
public class OAuth2RedirectValidator {

	private final AllowedDomainsPolicy allowedDomainsPolicy;

	public OAuth2RedirectValidator(AllowedDomainsPolicy allowedDomainsPolicy) {
		this.allowedDomainsPolicy = allowedDomainsPolicy;
	}

	/**
	 * 요청의 Origin/Referer가 허용된 도메인인지 검증하여 안전한 리다이렉트 URL 반환
	 * 
	 * @param request HTTP 요청
	 * @return 안전한 리다이렉트 base URL, 허용되지 않은 도메인이면 null
	 */
	public String getSafeRedirectBase(HttpServletRequest request) {
		String origin = request.getHeader("Origin");
		String referer = request.getHeader("Referer");

		log.info("OAuth2 리다이렉트 검증 시작 - Origin: {}, Referer: {}", origin, referer);

		// 1. 세션에서 원본 도메인 복원 시도 (OAuth2 콜백의 경우)
		String sessionOrigin = CustomAuthorizationRequestResolver.getAndRemoveOriginFromSession(request);
		if (sessionOrigin != null) {
			boolean isAllowed = allowedDomainsPolicy.isAllowedOrigin(sessionOrigin);
			log.info("세션에서 원본 도메인 복원 - URL: {}, 허용 여부: {}", sessionOrigin, isAllowed);
			if (isAllowed) {
				return sessionOrigin;
			}
		}

		// 2. Origin 헤더 확인
		if (origin != null) {
			boolean isAllowed = allowedDomainsPolicy.isAllowedOrigin(origin);
			log.info("Origin 검증 - URL: {}, 허용 여부: {}", origin, isAllowed);
			if (isAllowed) {
				return origin;
			}
		}

		// 3. Referer 헤더 확인
		if (referer != null) {
			try {
				URI refererUri = URI.create(referer);
				String refererOrigin = refererUri.getScheme() + "://" + refererUri.getHost()
					+ (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
				
				// Google OAuth 콜백인 경우 스킵 (이미 세션에서 확인함)
				if ("https://accounts.google.com".equals(refererOrigin)) {
					log.info("Google OAuth 콜백 감지 - 세션에서 원본 도메인을 찾지 못했으므로 실패");
				} else {
					boolean isAllowed = allowedDomainsPolicy.isAllowedOrigin(refererOrigin);
					log.info("Referer 검증 - 원본 URL: {}, 추출된 Origin: {}, 허용 여부: {}", referer, refererOrigin, isAllowed);
					if (isAllowed) {
						return refererOrigin;
					}
				}
			} catch (IllegalArgumentException e) {
				log.warn("잘못된 Referer URI 형식: {}", referer, e);
			}
		}

		log.warn("OAuth2 리다이렉트 검증 실패 - 허용되지 않은 도메인입니다.");
		return null;
	}
} 