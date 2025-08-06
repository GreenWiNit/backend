package com.example.green.domain.auth;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.exception.WithdrawnMemberAccessException;
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
	private final OAuth2RedirectValidator oauth2RedirectValidator;

	public OAuth2FailureHandler(
		TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl,
		OAuth2RedirectValidator oauth2RedirectValidator) {
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
		this.oauth2RedirectValidator = oauth2RedirectValidator;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		log.info("OAuth2 인증 실패: {}", exception.getMessage());

		String redirectBase = oauth2RedirectValidator.getSafeRedirectBase(request);
		
		if (redirectBase == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
			return;
		}

		// 탈퇴한 사용자 재가입 시도 감지
		if (exception.getCause() instanceof WithdrawnMemberAccessException) {
			log.warn("탈퇴한 사용자 재가입 시도 감지: {}", exception.getMessage());
			String redirectUrl = redirectBase + "/login?error=withdrawn_user&message=" + 
								URLEncoder.encode("탈퇴한 회원은 동일한 SNS 계정으로 재가입할 수 없습니다.", "UTF-8");
			response.sendRedirect(redirectUrl);
			return;
		}

		// 일반적인 인증 실패
		String redirectUrl = redirectBase + "/login?error=auth_failed";
		response.sendRedirect(redirectUrl);
	}
}

