package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.controller.docs.AuthControllerDocs;
import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
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
	public ResponseEntity<TokenResponseDto> signup(
		@RequestBody SignupRequestDto request,
		HttpServletRequest httpRequest,
		HttpServletResponse response
	) {
		log.info("[SIGNUP] tempToken={}, nickname={}", request.tempToken());

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

	@Override
	@PublicApi
	@PostMapping("/refresh")
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

	@Override
	@AuthenticatedApi(reason = "로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/logout")
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


	@Override
	@AuthenticatedApi(reason = "모든 디바이스 로그아웃은 로그인한 사용자만 가능합니다")
	@PostMapping("/logout-all")
	public ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();

		tokenService.revokeAllRefreshTokens(memberKey);
		authService.logoutAllDevices(memberKey);
		WebUtils.removeRefreshTokenCookie(response);

		log.info("[LOGOUT-ALL] User {} logged out from all devices", memberKey);
		return ResponseEntity.ok().build();
	}
}
