package com.example.green.domain.auth;

import java.net.URI;

import org.springframework.stereotype.Component;

import com.example.green.global.config.AllowedDomainsPolicy;

import jakarta.servlet.http.HttpServletRequest;

/**
 * OAuth2 리다이렉트 보안 검증 서비스
 */
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

		// Origin 헤더 확인
		if (origin != null && allowedDomainsPolicy.isAllowedOrigin(origin)) {
			return origin;
		}

		// Referer 헤더 확인
		if (referer != null) {
			try {
				URI refererUri = URI.create(referer);
				String refererOrigin = refererUri.getScheme() + "://" + refererUri.getHost()
					+ (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
				if (allowedDomainsPolicy.isAllowedOrigin(refererOrigin)) {
					return refererOrigin;
				}
			} catch (IllegalArgumentException ignored) {
				// 잘못된 URI 형식인 경우 무시
			}
		}

		return null;
	}
} 