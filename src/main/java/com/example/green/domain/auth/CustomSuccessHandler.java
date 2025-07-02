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

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.OAuth2UserInfoDto;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;

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

		CustomOAuth2UserDto customUserDetails = (CustomOAuth2UserDto)authentication.getPrincipal();
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
	private void handleNewUser(CustomOAuth2UserDto user, HttpServletResponse response) throws IOException {
		OAuth2UserInfoDto oauth2UserInfoDto = user.getUserDto().oauth2UserInfoDto();

		// 임시 토큰 생성
		String tempToken = tokenService.createTemporaryToken(
			oauth2UserInfoDto.email(),
			oauth2UserInfoDto.name(),
			oauth2UserInfoDto.profileImageUrl(),
			oauth2UserInfoDto.provider(),
			oauth2UserInfoDto.providerId()
		);

		// URL 인코딩
		String encodedToken = URLEncoder.encode(tempToken, StandardCharsets.UTF_8);

		// 프론트엔드로 리다이렉트
		String redirectUrl = frontendBaseUrl + "/signup?tempToken=" + encodedToken;

		log.info("신규 사용자 임시 토큰 생성 완료, 회원가입 페이지로 리다이렉트: {}", oauth2UserInfoDto.email());
		response.sendRedirect(redirectUrl);
	}

	// 기존 사용자 처리 - AccessToken/RefreshToken 발급
	private void handleExistingUser(CustomOAuth2UserDto user, String role, HttpServletResponse response)
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
		Cookie refreshCookie = WebUtils.createRefreshTokenCookie(
			refreshToken,
			WebUtils.isLocalDevelopment(frontendBaseUrl) ? false : true,
			7 * 24 * 60 * 60 // 7일
		);
		response.addCookie(refreshCookie);

		// AccessToken과 사용자 정보를 쿼리 파라미터로 전달
		String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
		String encodedUserInfo = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);

		// 프론트엔드로 리다이렉트
		String redirectUrl = frontendBaseUrl + "/login/success?accessToken=" + encodedAccessToken
			+ "&userName=" + encodedUserInfo;

		log.info("기존 사용자 로그인 성공, AccessToken/RefreshToken 발급 완료: {}", username);
		response.sendRedirect(redirectUrl);
	}

}
