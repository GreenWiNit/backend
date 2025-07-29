package com.example.green.domain.auth.security;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private static final String ORIGIN_DOMAIN_SESSION_KEY = "oauth2_origin_domain";

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        saveOriginToSession(request, authorizationRequest);
        return authorizationRequest;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        saveOriginToSession(request, authorizationRequest);
        return authorizationRequest;
    }

    private void saveOriginToSession(HttpServletRequest request, OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return;
        }

        String origin = extractOrigin(request);
        if (origin != null) {
            HttpSession session = request.getSession();
            session.setAttribute(ORIGIN_DOMAIN_SESSION_KEY, origin);
            log.info("OAuth2 인증 시작 - 원본 도메인을 세션에 저장: {}", origin);
        }
    }

    private String extractOrigin(HttpServletRequest request) {

        String origin = request.getHeader("Origin");
        if (origin != null) {
            return origin;
        }

        String referer = request.getHeader("Referer");
        if (referer != null) {
            try {
                java.net.URI refererUri = java.net.URI.create(referer);
                return refererUri.getScheme() + "://" + refererUri.getHost() +
                       (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
            } catch (Exception e) {
                log.warn("Referer URI 파싱 실패: {}", referer, e);
            }
        }

        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (forwardedHost != null) {
            String protocol = "https".equals(request.getHeader("X-Forwarded-Proto")) ? "https" : "http";
            return protocol + "://" + forwardedHost;
        }

        log.warn("원본 도메인을 추출할 수 없습니다.");
        return null;
    }

    public static String getAndRemoveOriginFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String origin = (String) session.getAttribute(ORIGIN_DOMAIN_SESSION_KEY);
            if (origin != null) {
                session.removeAttribute(ORIGIN_DOMAIN_SESSION_KEY);
                return origin;
            }
        }
        return null;
    }
} 