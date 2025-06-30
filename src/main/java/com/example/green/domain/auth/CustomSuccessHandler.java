package com.example.green.domain.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.dto.OAuth2UserInfo;
import com.example.green.domain.auth.service.TokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenService tokenService;
	private final String frontendBaseUrl;

	public CustomSuccessHandler(
		TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl) {
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User customUserDetails = (CustomOAuth2User)authentication.getPrincipal();
		String username = customUserDetails.getUsername();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		// 신규 사용자인지 확인
		if (customUserDetails.getUserDto().isNewUser()) {
			handleNewUser(customUserDetails, response);
		} else {
			handleExistingUser(customUserDetails, role, response);
		}
	}

	// 신규 사용자 처리 - 임시 토큰 생성 후 회원가입 페이지로 리다이렉트
	private void handleNewUser(CustomOAuth2User user, HttpServletResponse response) throws IOException {
		OAuth2UserInfo oauth2UserInfo = user.getUserDto().oauth2UserInfo();
		
		// 임시 토큰 생성
		String tempToken = tokenService.createTemporaryToken(
			oauth2UserInfo.email(),
			oauth2UserInfo.name(),
			oauth2UserInfo.profileImageUrl()
		);
		
		// URL 인코딩
		String encodedToken = URLEncoder.encode(tempToken, StandardCharsets.UTF_8);
		
		// 회원가입 페이지로 리다이렉트
		String redirectUrl = "/signup.html?tempToken=" + encodedToken;
		
		log.info("신규 사용자 임시 토큰 생성 완료, 회원가입 페이지로 리다이렉트: {}", oauth2UserInfo.email());
		response.sendRedirect(redirectUrl);
	}

	// 기존 사용자 처리 - AccessToken/RefreshToken 발급
	private void handleExistingUser(CustomOAuth2User user, String role, HttpServletResponse response) 
		throws IOException {
		
		String username = user.getUsername();
		
		// AccessToken과 RefreshToken 생성
		String accessToken = tokenService.createAccessToken(username, role);
		String refreshToken = tokenService.createRefreshToken(
			username, 
			"Web Browser", // 디바이스 정보 (추후 User-Agent에서 추출 가능)
			"Unknown IP"   // IP 주소 (추후 HttpServletRequest에서 추출 가능)
		);
		
		// RefreshToken을 HTTP-Only 쿠키에 저장
		response.addCookie(createRefreshTokenCookie(refreshToken));
		
		// AccessToken과 사용자 정보를 쿼리 파라미터로 홈페이지에 전달
		String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
		String encodedUserInfo = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);
		
		String redirectUrl = "/oauth-test.html?success=true&accessToken=" + encodedAccessToken 
			+ "&userName=" + encodedUserInfo;
		
		log.info("기존 사용자 로그인 성공, AccessToken/RefreshToken 발급 완료: {}", username);
		response.sendRedirect(redirectUrl);
	}

	// RefreshToken용 쿠키 생성 (HTTP-Only, 7일)
	private Cookie createRefreshTokenCookie(String refreshToken) {
		Cookie cookie = new Cookie("RefreshToken", refreshToken);
		cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		cookie.setPath("/");
		cookie.setHttpOnly(true); // 보안: JavaScript 접근 불가
		//cookie.setSecure(true); // HTTPS 환경에서만 활성화 (현재 localhost 테스트를 위해 비활성화)
		
		return cookie;
	}

	// 기존 Authorization 쿠키 생성 메서드 (임시 호환용)
	private Cookie createCookie(String key, String value) {
		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(60 * 60 * 60); //TODO yml로 이동 예정
		//cookie.setSecure(true); // HTTPS 환경에서만 활성화 (현재 localhost 테스트를 위해 비활성화)
		cookie.setPath("/");
		
		// HttpOnly 설정
		// true: 보안 강화 (XSS 공격 방지), JavaScript에서 쿠키 접근 불가 - 운영 환경 권장
		// false: JavaScript에서 쿠키 접근 가능 - 개발/테스트 환경에서만 사용
		// TODO: 프로파일별로 설정 분리 필요 (local: false, prod: true)
		cookie.setHttpOnly(false);

		return cookie;
	}
}
