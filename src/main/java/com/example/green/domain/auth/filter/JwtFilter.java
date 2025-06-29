package com.example.green.domain.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.dto.UserDto;
import com.example.green.domain.auth.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 쿠키들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
		String authorization = null;
		Cookie[] cookies = request.getCookies();

		// 쿠키가 null인 경우 체크
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				log.debug("Cookie name: {}", cookie.getName());
				if (cookie.getName().equals("Authorization")) {
					authorization = cookie.getValue();
					break;
				}
			}
		}

		// Authorization 헤더 검증
		if (authorization == null) {
			log.debug("JWT token not found in cookies");
			filterChain.doFilter(request, response);
			return; // 조건이 해당되면 메소드 종료 (필수)
		}

		// 토큰
		String token = authorization;

		// 토큰 소멸 시간 검증
		if (jwtUtil.isExpired(token)) {
			log.debug("JWT token is expired");
			filterChain.doFilter(request, response);
			return; // 조건이 해당되면 메소드 종료 (필수)
		}

		// 토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);

		UserDto userDto = new UserDto(role, username, username);

		// UserDetails에 회원 정보 객체 담기
		CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);

		// 스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(
			customOAuth2User, null, customOAuth2User.getAuthorities());

		// 세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);

		log.debug("JWT authentication completed for user: {}", username);
		filterChain.doFilter(request, response);
	}
} 