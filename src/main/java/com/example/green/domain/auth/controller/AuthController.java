package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.controller.docs.AuthControllerDocs;
import com.example.green.domain.auth.controller.message.AuthResponseMessage;
import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.exception.AuthExceptionMessage;
import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.global.api.ApiTemplate;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;
import com.example.green.global.security.annotation.AuthenticatedApi;
import com.example.green.global.security.annotation.PublicApi;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

	private final AuthService authService;
	private final TokenService tokenService;

	@Override
	@PublicApi
	@PostMapping("/signup")
	@Deprecated
	public ResponseEntity<TokenResponseDto> signup(
		@RequestBody SignupRequestDto request,
		HttpServletRequest httpRequest,
		HttpServletResponse response
	) {
		log.info("[SIGNUP] tempToken={}, nickname={}", request.tempToken(),request.nickname());

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

		log.info("[SIGNUP] completed for memberKey={}", memberKey);
		return ResponseEntity.ok(new TokenResponseDto(
			accessToken.getValue(),
			memberKey,
			tempToken.getName()
		));
	}

	@PublicApi
	@PostMapping("/v2/signup")
	public ApiTemplate<TokenResponseDto> signupV2(
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

	@Override
	@PublicApi
	@PostMapping("/refresh")
	@Deprecated
	public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request) {
		String refreshTokenString = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshTokenString == null) {
			log.warn("[REFRESH] Missing TokenManager cookie");
			return ResponseEntity.badRequest().build();
		}

		String memberKey = tokenService.getMemberKey(refreshTokenString);
		String currentIpAddress = WebUtils.extractClientIp(request);
		String newAccessTokenString = tokenService.refreshAccessToken(refreshTokenString, ROLE_USER, currentIpAddress);
		AccessToken newAccessToken = AccessToken.from(newAccessTokenString, tokenService);

		log.info("[REFRESH] issued new AccessToken for memberKey={} from IP={}", memberKey, currentIpAddress);
		return ResponseEntity.ok(new TokenResponseDto(newAccessToken.getValue(), memberKey, null));
	}

	@PublicApi
	@PostMapping("/v2/refresh")
	public ApiTemplate<TokenResponseDto> refreshTokenV2(HttpServletRequest request) {
		String refreshTokenString = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshTokenString == null) {
			log.warn("[REFRESH_V2] Missing TokenManager cookie");
			throw new BusinessException(AuthExceptionMessage.REFRESH_TOKEN_MISSING);
		}

		String memberKey = tokenService.getMemberKey(refreshTokenString);
		String currentIpAddress = WebUtils.extractClientIp(request);
		String newAccessTokenString = tokenService.refreshAccessToken(refreshTokenString, ROLE_USER, currentIpAddress);
		AccessToken newAccessToken = AccessToken.from(newAccessTokenString, tokenService);

		log.info("[REFRESH_V2] issued new AccessToken for memberKey={} from IP={}", memberKey, currentIpAddress);
		return ApiTemplate.ok(AuthResponseMessage.TOKEN_REFRESHED, new TokenResponseDto(newAccessToken.getValue(), memberKey, null));
	}

	@Override
	@AuthenticatedApi(reason = "로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/logout")
	@Deprecated
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();

		String refreshToken = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshToken != null) {
			tokenService.revokeRefreshToken(refreshToken);
		}

		authService.logout(memberKey);
		WebUtils.removeRefreshTokenCookie(response);

		log.info("[LOGOUT] User {} logged out", memberKey);
		return ResponseEntity.ok().build();
	}


	@AuthenticatedApi(reason = "로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/v2/logout")
	public ApiTemplate<Void> logoutV2(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();

		String refreshToken = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshToken != null) {
			tokenService.revokeRefreshToken(refreshToken);
		}

		authService.logout(memberKey);
		WebUtils.removeRefreshTokenCookie(response);

		log.info("[LOGOUT_V2] User {} logged out", memberKey);
		return ApiTemplate.ok(AuthResponseMessage.LOGOUT_SUCCESS);
	}

	@Override
	@AuthenticatedApi(reason = "모든 디바이스 로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/logout-all")
	@Deprecated
	public ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();

		tokenService.revokeAllRefreshTokens(memberKey);
		authService.logoutAllDevices(memberKey);
		WebUtils.removeRefreshTokenCookie(response);

		log.info("[LOGOUT-ALL] User {} logged out from all devices", memberKey);
		return ResponseEntity.ok().build();
	}

	@AuthenticatedApi(reason = "모든 디바이스 로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/v2/logout-all")
	public ApiTemplate<Void> logoutAllV2(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();

		tokenService.revokeAllRefreshTokens(memberKey);
		authService.logoutAllDevices(memberKey);
		WebUtils.removeRefreshTokenCookie(response);

		log.info("[LOGOUT-ALL_V2] User {} logged out from all devices", memberKey);
		return ApiTemplate.ok(AuthResponseMessage.LOGOUT_ALL_SUCCESS);
	}
}
