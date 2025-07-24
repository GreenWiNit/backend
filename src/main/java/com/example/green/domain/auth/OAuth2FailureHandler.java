package com.example.green.domain.auth;

import java.io.IOException;
import java.net.URI;
import java.util.List;

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

		String redirectBase = getSafeRedirectBase(request);
		
		String redirectUrl;
		if (redirectBase == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
			return;
		} else {
			redirectUrl = redirectBase + "/login?error=auth_failed";
		}

		response.sendRedirect(redirectUrl);
	}

	private String getSafeRedirectBase(HttpServletRequest request) {
		List<String> allowedOrigins = List.of(
			"https://greenwinit.pages.dev",
			"https://www.greenwinit.store",
			"http://localhost:5173",
			"http://localhost:5174",
			"http://localhost:3000"
		);

		String origin = request.getHeader("Origin");
		String referer = request.getHeader("Referer");

		if (origin != null && allowedOrigins.contains(origin)) {
			return origin;
		}

		if (referer != null) {
			try {
				URI refererUri = URI.create(referer);
				String refererOrigin = refererUri.getScheme() + "://" + refererUri.getHost()
					+ (refererUri.getPort() != -1 ? ":" + refererUri.getPort() : "");
				if (allowedOrigins.contains(refererOrigin)) {
					return refererOrigin;
				}
			} catch (IllegalArgumentException ignored) {
				// 잘못된 URI 형식인 경우 무시
			}
		}

		return null;
	}
}

