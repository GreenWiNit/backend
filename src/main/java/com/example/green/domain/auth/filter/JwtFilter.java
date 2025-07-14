package com.example.green.domain.auth.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰 검증 필터
 * OAuth2 경로나 정적 리소스에는 적용되지 않으므로 경로 제외 로직이 불필요합니다.
 * - 유효한 토큰: SecurityContext에 인증 정보 저장
 * - 무효한/없는 토큰: SecurityContext 비워둠 → @PreAuthorize에서 최종 권한 검증
 */
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final int BEARER_PREFIX_LENGTH = 7;

	private final TokenService tokenService;

	public JwtFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessTokenString = extractAccessTokenFromHeader(request);

		if (accessTokenString == null) {
			log.debug("AccessToken not found in Authorization header for path: {}", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		try {
			AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

			if (!accessToken.isValid()) {
				log.debug("Invalid AccessToken - proceeding without authentication for @PreAuthorize evaluation");
				// SecurityContext를 비워두고 계속 진행하여 @PreAuthorize에서 권한 검증하도록 함
				filterChain.doFilter(request, response);
				return;
			}

			Authentication authToken = tokenService.createAuthentication(accessTokenString);

			SecurityContextHolder.getContext().setAuthentication(authToken);

			log.debug("JWT authentication completed for user: {}", accessToken.getMemberKey());

		} catch (BusinessException e) {
			if (e.getExceptionMessage() == GlobalExceptionMessage.JWT_TOKEN_EXPIRED) {
				log.debug("AccessToken expired - proceeding without authentication for @PreAuthorize evaluation");
			} else {
				log.debug(
					"AccessToken validation failed - proceeding without authentication for @PreAuthorize evaluation");
			}
			// 토큰이 만료되거나 유효하지 않더라도 SecurityContext를 비워두고 계속 진행
			// @PreAuthorize에서 최종 권한 검증을 수행하도록 함
			filterChain.doFilter(request, response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String extractAccessTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX_LENGTH);
		}
		return null;
	}
}

