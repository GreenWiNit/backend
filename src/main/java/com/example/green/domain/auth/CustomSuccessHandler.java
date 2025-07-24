package com.example.green.domain.auth;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.OAuth2UserInfoDto;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
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

	public CustomSuccessHandler(TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl) {
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication)
		throws IOException, ServletException {

		CustomOAuth2UserDto customUserDetails = (CustomOAuth2UserDto) authentication.getPrincipal();
		String memberKey = customUserDetails.getMemberKey();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();
		String role = auth.getAuthority();

		if (customUserDetails.getUserDto().isNewUser()) {
			handleNewUser(customUserDetails, response, request);
		} else {
			handleExistingUser(customUserDetails, role, response, request);
		}
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

	private void handleNewUser(CustomOAuth2UserDto user,
		HttpServletResponse response,
		HttpServletRequest request)
		throws IOException {
		OAuth2UserInfoDto oauth2UserInfoDto = user.getUserDto().oauth2UserInfoDto();

		String tempTokenString = tokenService.createTemporaryToken(
			oauth2UserInfoDto.email(),
			oauth2UserInfoDto.name(),
			oauth2UserInfoDto.profileImageUrl(),
			oauth2UserInfoDto.provider(),
			oauth2UserInfoDto.providerId()
		);
		TempToken tempToken = TempToken.from(tempTokenString, tokenService);

		String encodedToken = URLEncoder.encode(tempToken.getValue(), StandardCharsets.UTF_8);
		String redirectBase = getSafeRedirectBase(request);

		if (redirectBase == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
			return;
		}

		String redirectUrl = redirectBase + "/signup?tempToken=" + encodedToken;

		log.info("신규 사용자 임시 토큰 생성 완료, 회원가입 페이지로 리다이렉트: {} -> {}", oauth2UserInfoDto.email(), redirectUrl);
		response.sendRedirect(redirectUrl);
	}

	private void handleExistingUser(CustomOAuth2UserDto user,
		String role,
		HttpServletResponse response,
		HttpServletRequest request)
		throws IOException {

		String memberKey = user.getMemberKey();
		String redirectBase = getSafeRedirectBase(request);

		if (redirectBase == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
			return;
		}

		String refreshTokenString = tokenService.createRefreshToken(memberKey, "Web Browser", "Unknown IP");
		String accessTokenString = tokenService.createAccessToken(memberKey, role);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		boolean isLocalhost = redirectBase.startsWith("http://localhost");
		boolean secureFlag = !isLocalhost;

		Cookie refreshCookie = WebUtils.createRefreshTokenCookie(
			refreshTokenString,
			secureFlag,
			7 * 24 * 60 * 60
		);
		response.addCookie(refreshCookie);

		String encodedAccessToken = URLEncoder.encode(accessToken.getValue(), StandardCharsets.UTF_8);
		String encodedUserInfo = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);

		String redirectUrl = redirectBase + "/?accessToken=" + encodedAccessToken + "&userName=" + encodedUserInfo;

		log.info("기존 사용자 로그인 성공, AccessToken/TokenManager 발급 완료: {} -> {}", memberKey, redirectUrl);
		response.sendRedirect(redirectUrl);
	}

}