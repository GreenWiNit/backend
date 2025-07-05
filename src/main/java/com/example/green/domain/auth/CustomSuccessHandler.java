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
import com.example.green.domain.auth.model.vo.AccessToken;
import com.example.green.domain.auth.model.vo.TempToken;
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

		// TempToken VO 생성
		String tempTokenString = tokenService.createTemporaryToken(
			oauth2UserInfoDto.email(),
			oauth2UserInfoDto.name(),
			oauth2UserInfoDto.profileImageUrl(),
			oauth2UserInfoDto.provider(),
			oauth2UserInfoDto.providerId()
		);
		TempToken tempToken = TempToken.from(tempTokenString, tokenService);

		// URL 인코딩
		String encodedToken = URLEncoder.encode(tempToken.getValue(), StandardCharsets.UTF_8);

		// 환경별 리다이렉트 분기
		String redirectUrl;
		if (WebUtils.isLocalDevelopment(frontendBaseUrl)) {
			// 개발 환경: 백엔드 테스트 페이지
			redirectUrl = "/signup.html?tempToken=" + encodedToken;
		} else {
			// 프로덕션 환경: 실제 프론트엔드
			redirectUrl = frontendBaseUrl + "/signup?tempToken=" + encodedToken;
		}

		log.info("신규 사용자 임시 토큰 생성 완료, 회원가입 페이지로 리다이렉트: {}", oauth2UserInfoDto.email());
		response.sendRedirect(redirectUrl);
	}

	// 기존 사용자 처리 - AccessToken/TokenManager 발급
	private void handleExistingUser(CustomOAuth2UserDto user, String role, HttpServletResponse response)
		throws IOException {

		String username = user.getUsername();

		// TokenManager 먼저 생성 (기존 토큰 정리 + tokenVersion 증가)
		String refreshTokenString = tokenService.createRefreshToken(
			username,
			"Web Browser", // 디바이스 정보 (추후 User-Agent에서 추출 가능)
			"Unknown IP"   // IP 주소 (추후 HttpServletRequest에서 추출 가능)
		);

		// AccessToken 나중 생성 (새로운 tokenVersion으로)
		String accessTokenString = tokenService.createAccessToken(username, role);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		// RefreshToken을 HTTP-Only 쿠키에 저장
		Cookie refreshCookie = WebUtils.createRefreshTokenCookie(
			refreshTokenString,
			WebUtils.isLocalDevelopment(frontendBaseUrl) ? false : true,
			7 * 24 * 60 * 60 // 7일
		);
		response.addCookie(refreshCookie);

		// AccessToken과 사용자 정보를 쿼리 파라미터로 전달
		String encodedAccessToken = URLEncoder.encode(accessToken.getValue(), StandardCharsets.UTF_8);
		String encodedUserInfo = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);

		// 환경별 리다이렉트 분기
		String redirectUrl;
		if (WebUtils.isLocalDevelopment(frontendBaseUrl)) {
			// 개발 환경: 백엔드 테스트 페이지
			redirectUrl = "/oauth-test.html?success=true&accessToken=" + encodedAccessToken
						  + "&userName=" + encodedUserInfo;
		} else {
			// 프로덕션 환경: 실제 프론트엔드
			redirectUrl = frontendBaseUrl + "/login/success?accessToken=" + encodedAccessToken
						  + "&userName=" + encodedUserInfo;
		}

		log.info("기존 사용자 로그인 성공, AccessToken/TokenManager 발급 완료: {}", username);
		response.sendRedirect(redirectUrl);
	}

}
