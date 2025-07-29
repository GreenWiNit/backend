package com.example.green.domain.auth;

import java.net.URI;

import org.springframework.stereotype.Component;

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

		// Origin 헤더 확인
		if (origin != null) {
			boolean isAllowed = allowedDomainsPolicy.isAllowedOrigin(origin);
			log.info("Origin 검증 - URL: {}, 허용 여부: {}", origin, isAllowed);
			if (isAllowed) {
				return origin;
			}
		}

		// Referer 헤더 확인
		if (referer != null) {
			try {
				URI refererUri = URI.create(referer);
				String refererOrigin = refererUri.getScheme() + "://" + refererUri.getHost()
					+ (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
				boolean isAllowed = allowedDomainsPolicy.isAllowedOrigin(refererOrigin);
				log.info("Referer 검증 - 원본 URL: {}, 추출된 Origin: {}, 허용 여부: {}", referer, refererOrigin, isAllowed);
				if (isAllowed) {
					return refererOrigin;
				}
			} catch (IllegalArgumentException e) {
				log.warn("잘못된 Referer URI 형식: {}", referer, e);
			}
		}

		log.warn("OAuth2 리다이렉트 검증 실패 - 허용되지 않은 도메인입니다.");
		return null;
	}
} 