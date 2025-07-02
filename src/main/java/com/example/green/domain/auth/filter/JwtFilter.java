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

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final int BEARER_PREFIX_LENGTH = 7;
	private static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

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

		String accessTokenString = extractAccessTokenFromHeader(request);

		if (accessTokenString == null) {
			log.debug("AccessToken not found in Authorization header");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

			if (!accessToken.isValid()) {
				log.debug("Invalid AccessToken");
				handleInvalidToken(response);
				return;
			}

			String username = accessToken.getUsername();
			String role = accessToken.getRole();

			UserDto userDto = UserDto.forExistingUser(role, username, username);
			CustomOAuth2UserDto customOAuth2User = new CustomOAuth2UserDto(userDto);

			Authentication authToken = new UsernamePasswordAuthenticationToken(
				customOAuth2User, null, customOAuth2User.getAuthorities());

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

	private String extractAccessTokenFromHeader(HttpServletRequest request) {
		String authHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
			return authHeader.substring(BEARER_PREFIX_LENGTH);
		}
		return null;
	}

	private void handleExpiredToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(JSON_CONTENT_TYPE);
		response.getWriter().write(
			String.format("{\"error\":\"%s\",\"message\":\"%s\"}",
				ERROR_TOKEN_EXPIRED, MESSAGE_TOKEN_EXPIRED)
		);
		log.debug("AccessToken expired - 401 Unauthorized response sent");
	}

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
