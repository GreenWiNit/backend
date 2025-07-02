package com.example.green.domain.auth.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.UserDto;
import com.example.green.domain.auth.model.vo.AccessToken;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	// ================================
	// HTTP 관련 (이 클래스에서만 사용)
	// ================================
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final int BEARER_PREFIX_LENGTH = 7;
	private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

	// ================================
	// 에러 응답 관련 (이 클래스에서만 사용)
	// ================================
	private static final String ERROR_TOKEN_EXPIRED = "TOKEN_EXPIRED";
	private static final String ERROR_INVALID_TOKEN = "INVALID_TOKEN";
	private static final String MESSAGE_TOKEN_EXPIRED = "Access token has expired. Please refresh your token.";
	private static final String MESSAGE_INVALID_TOKEN = "Invalid access token. Please login again.";

	private final TokenService tokenService;

	public JwtFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Authorization 헤더에서 AccessToken 문자열 추출
		String accessTokenString = extractAccessTokenFromHeader(request);

		// AccessToken이 없는 경우 다음 필터로 진행
		if (accessTokenString == null) {
			log.debug("AccessToken not found in Authorization header");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// AccessToken VO로 감싸서 타입 안전성 확보
			AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

			// VO의 유효성 검증 메서드 사용 (내부에서 토큰 타입도 함께 검증)
			if (!accessToken.isValid()) {
				log.debug("Invalid AccessToken");
				handleInvalidToken(response);
				return;
			}

			// VO 메서드로 안전하게 사용자 정보 추출
			String username = accessToken.getUsername();
			String role = accessToken.getRole();

			// UserDto 생성 (기존 사용자로 처리)
			UserDto userDto = UserDto.forExistingUser(role, username, username);

			// CustomOAuth2UserDto 생성
			CustomOAuth2UserDto customOAuth2User = new CustomOAuth2UserDto(userDto);

			// Spring Security 인증 토큰 생성
			Authentication authToken = new UsernamePasswordAuthenticationToken(
				customOAuth2User, null, customOAuth2User.getAuthorities());

			// SecurityContext에 인증 정보 설정
			SecurityContextHolder.getContext().setAuthentication(authToken);

			log.debug("JWT authentication completed for user: {}", username);

		} catch (BusinessException e) {
			// JWT 관련 예외 처리 (VO 내부에서도 발생 가능)
			if (e.getExceptionMessage() == GlobalExceptionMessage.JWT_TOKEN_EXPIRED) {
				handleExpiredToken(response);
			} else {
				handleInvalidToken(response);
			}
			return;
		}

		filterChain.doFilter(request, response);
	}

	// Authorization 헤더에서 AccessToken 추출
	private String extractAccessTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX_LENGTH);
		}
		return null;
	}

	// 만료된 토큰 처리
	private void handleExpiredToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(JSON_CONTENT_TYPE);
		response.getWriter().write(
			String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
				ERROR_TOKEN_EXPIRED, MESSAGE_TOKEN_EXPIRED)
		);
		log.debug("AccessToken expired - 401 Unauthorized response sent");
	}

	// 유효하지 않은 토큰 처리
	private void handleInvalidToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(JSON_CONTENT_TYPE);
		response.getWriter().write(
			String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
				ERROR_INVALID_TOKEN, MESSAGE_INVALID_TOKEN)
		);
		log.debug("Invalid AccessToken - 401 Unauthorized response sent");
	}
}
