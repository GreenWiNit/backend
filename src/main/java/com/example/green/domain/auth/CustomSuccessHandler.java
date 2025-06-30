package com.example.green.domain.auth;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.utils.JwtUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final String frontendBaseUrl;

	public CustomSuccessHandler(
		JwtUtil jwtUtil,
		@Value("${app.frontend.base-url}") String frontendBaseUrl) {
		this.jwtUtil = jwtUtil;
		this.frontendBaseUrl = frontendBaseUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws
		IOException,
		ServletException {

		//OAuth2User
		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();

		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		String token = jwtUtil.createJwt(username, role);

		response.addCookie(createCookie("Authorization", token));

		// 테스트용: OAuth2 테스트 페이지로 리다이렉트 (success 파라미터 추가)
		response.sendRedirect("/oauth-test.html?success=true");
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60); //TODO yml로 이동 예정
		//cookie.setSecure(true); // HTTPS 환경에서만 활성화 (현재 localhost 테스트를 위해 비활성화)
		cookie.setPath("/");

		// HttpOnly 설정
		// true: 보안 강화 (XSS 공격 방지), JavaScript에서 쿠키 접근 불가 - 운영 환경 권장
		// false: JavaScript에서 쿠키 접근 가능 - 개발/테스트 환경에서만 사용
		// TODO: true로 바꾸기
		cookie.setHttpOnly(false);

		return cookie;
	}
}
