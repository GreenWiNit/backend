package com.example.green.domain.auth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.service.TokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final TokenService tokenService;
	private final String frontendBaseUrl;

	public OAuth2FailureHandler(
		TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl) {
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {
		
		log.info("OAuth2 인증 실패 - 신규 사용자로 추정: {}", exception.getMessage());
		
		// 신규 사용자인 경우 회원가입 페이지로 리다이렉트
		// 실제로는 CustomOAuth2UserService에서 신규 사용자 정보를 처리해야 함
		// 여기서는 일반적인 실패 처리만 수행
		String redirectUrl = "/oauth-test.html?error=new_user";
		response.sendRedirect(redirectUrl);
	}

	// 신규 사용자 정보로 임시 토큰 생성 후 회원가입 페이지로 리다이렉트
	public void handleNewUser(String email, String name, String profileImageUrl, HttpServletResponse response) 
		throws IOException {
		
		// 임시 토큰 생성
		String tempToken = tokenService.createTemporaryToken(email, name, profileImageUrl);
		
		// 회원가입 페이지로 리다이렉트 (임시 토큰 포함)
		String encodedToken = URLEncoder.encode(tempToken, StandardCharsets.UTF_8);
		String redirectUrl = "/signup.html?tempToken=" + encodedToken;
		
		log.info("신규 사용자 임시 토큰 생성 완료, 회원가입 페이지로 리다이렉트: {}", email);
		response.sendRedirect(redirectUrl);
	}
} 