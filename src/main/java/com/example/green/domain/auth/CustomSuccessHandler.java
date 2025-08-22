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
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.domain.auth.OAuth2RedirectValidator;

import jakarta.servlet.ServletException;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final TokenService tokenService;
	private final String frontendBaseUrl;
	private final OAuth2RedirectValidator oauth2RedirectValidator;

	public CustomSuccessHandler(TokenService tokenService,
		@Value("${app.frontend.base-url}") String frontendBaseUrl,
		OAuth2RedirectValidator oauth2RedirectValidator) {
		this.tokenService = tokenService;
		this.frontendBaseUrl = frontendBaseUrl;
		this.oauth2RedirectValidator = oauth2RedirectValidator;
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
		String redirectBase = oauth2RedirectValidator.getSafeRedirectBase(request);

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
		String redirectBase = oauth2RedirectValidator.getSafeRedirectBase(request);

		if (redirectBase == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
			return;
		}

		String refreshTokenString = tokenService.createRefreshToken(memberKey, "Web Browser", "Unknown IP");
		String accessTokenString = tokenService.createAccessToken(memberKey, role);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		boolean isLocalhost = WebUtils.isLocalDevelopment(redirectBase);
		boolean secureFlag = !isLocalhost;

		// redirectBase에서 도메인 추출
		String domainHost = null;
		if (!isLocalhost) {
			try {
				java.net.URI uri = java.net.URI.create(redirectBase);
				String host = uri.getHost();
				if (host != null && !host.equals("localhost")) {
					domainHost = WebUtils.toRegistrableDomain(host);
					if (domainHost == null) {
						log.warn("도메인 산출 실패, Domain 미설정: host={}", host);
					}
				}
			} catch (Exception e) {
				log.warn("도메인 추출 실패: {}", redirectBase, e);
			}
		}

		ResponseCookie refreshCookie = WebUtils.createRefreshTokenResponseCookie(
			refreshTokenString,
			secureFlag,
			7 * 24 * 60 * 60,
			domainHost
		);
		response.addHeader("Set-Cookie", refreshCookie.toString());

		String encodedAccessToken = URLEncoder.encode(accessToken.getValue(), StandardCharsets.UTF_8);
		String encodedUserInfo = URLEncoder.encode(user.getName(), StandardCharsets.UTF_8);

		String redirectUrl = redirectBase + "/?accessToken=" + encodedAccessToken + "&userName=" + encodedUserInfo;
		if (isLocalhost) {
			redirectUrl = redirectBase + "&refreshToken=" + refreshTokenString;
		}

		log.info("기존 사용자 로그인 성공, AccessToken/TokenManager 발급 완료: {} -> {}", memberKey, redirectUrl);
		response.sendRedirect(redirectUrl);
	}

}