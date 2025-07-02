package com.example.green.domain.auth.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.UserDto;
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

	private final TokenService tokenService;

	public JwtFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// Authorization 헤더에서 AccessToken 추출
		String accessToken = extractAccessTokenFromHeader(request);

		// AccessToken이 없는 경우 다음 필터로 진행
		if (accessToken == null) {
			log.debug("AccessToken not found in Authorization header");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// AccessToken 검증
			if (!tokenService.validateToken(accessToken)) {
				log.debug("Invalid AccessToken");
				handleInvalidToken(response);
				return;
			}

			// 토큰 타입 검증 (AccessToken인지 확인)
			String tokenType = tokenService.getTokenType(accessToken);
			if (!"access".equals(tokenType)) {
				log.debug("Invalid token type: {}", tokenType);
				handleInvalidToken(response);
				return;
			}

			// 토큰에서 사용자 정보 추출
			String username = tokenService.getUsername(accessToken);
			String role = tokenService.getRole(accessToken);

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
			// JWT 관련 예외 처리
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
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7); // "Bearer " 제거
		}
		return null;
	}

	// 만료된 토큰 처리
	private void handleExpiredToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(
			"{\"error\":\"TOKEN_EXPIRED\",\"message\":\"Access token has expired. Please refresh your token.\"}"
		);
		log.debug("AccessToken expired - 401 Unauthorized response sent");
	}

	// 유효하지 않은 토큰 처리
	private void handleInvalidToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(
			"{\"error\":\"INVALID_TOKEN\",\"message\":\"Invalid access token. Please login again.\"}"
		);
		log.debug("Invalid AccessToken - 401 Unauthorized response sent");
	}
}
