package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.controller.message.AuthResponseMessage;
import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.security.annotation.PublicApi;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/auth")
@RequiredArgsConstructor
public class AuthV2Controller {

	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

	private final AuthService authService;
	private final TokenService tokenService;

	@PublicApi
	@PostMapping("/signup")
	public ApiTemplate<TokenResponseDto> signup(
		@RequestBody SignupRequestDto request,
		HttpServletRequest httpRequest,
		HttpServletResponse response
	) {
		log.info("[SIGNUP_V2] tempToken={}, nickname={}", request.tempToken(), request.nickname());

		TempToken tempToken = TempToken.from(request.tempToken(), tokenService);
		TempTokenInfoDto tempInfo = tempToken.extractUserInfo();

		String memberKey = authService.signup(tempInfo, request.nickname(), request.profileImageUrl());

		String refreshTokenString = tokenService.createRefreshToken(
			memberKey,
			WebUtils.extractDeviceInfo(httpRequest),
			WebUtils.extractClientIp(httpRequest)
		);
		Cookie cookie = WebUtils.createRefreshTokenCookie(
			refreshTokenString,
			WebUtils.isSecureRequest(httpRequest),
			REFRESH_TOKEN_MAX_AGE
		);
		response.addCookie(cookie);

		String accessTokenString = tokenService.createAccessToken(memberKey, ROLE_USER);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		log.info("[SIGNUP_V2] completed for memberKey={}", memberKey);
		
		TokenResponseDto tokenResponse = new TokenResponseDto(
			accessToken.getValue(),
			memberKey,
			tempToken.getName()
		);
		
		return ApiTemplate.ok(AuthResponseMessage.SIGNUP_SUCCESS, tokenResponse);
	}
}