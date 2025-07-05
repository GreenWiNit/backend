package com.example.green.domain.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;

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

		log.info("OAuth2 인증 실패: {}", exception.getMessage());

		// 환경별 리다이렉트 분기
		String redirectUrl;
		if (WebUtils.isLocalDevelopment(frontendBaseUrl)) {
			// 개발 환경: 백엔드 테스트 페이지
			redirectUrl = "/oauth-test.html?error=auth_failed";
		} else {
			// 프로덕션 환경: 실제 프론트엔드
			redirectUrl = frontendBaseUrl + "/login?error=auth_failed";
		}

		response.sendRedirect(redirectUrl);
	}
}

