package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "인증 API", description = "OAuth2 로그인, 회원가입, 토큰 관리 등 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

	private final AuthService authService;
	private final TokenService tokenService;

	@PublicApi
	@Operation(
		summary = "회원가입",
		description = "OAuth2 로그인 후 신규 사용자의 회원가입을 처리합니다. " + "임시 토큰에서 Google 계정 정보를 추출하고, 추가 정보를 받아 회원 등록을 완료합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원가입 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TokenResponseDto.class),
				examples = @ExampleObject(value = """
					{
					"accessToken": "eyJhbGciOiJIUzI1NiJ9...",
					"memberKey": "google_123456789",
					"userName": "홍길동"}
					"""))),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (임시 토큰 만료, 필수 필드 누락 등)",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					"error": "INVALID_REQUEST",
					"message": "임시 토큰이 만료되었습니다."
					}
					""")))
	})
	@PostMapping("/signup")
	public ResponseEntity<TokenResponseDto> signup(
		@Parameter(
			description = "회원가입 요청 정보 (임시 토큰, 닉네임, 프로필 이미지 URL)",
			required = true,
			content = @Content(
				schema = @Schema(implementation = SignupRequestDto.class),
				examples = @ExampleObject(value = """
					{
							"tempToken": "eyJhbGciOiJIUzI1NiJ9...",
							"nickname": "홍길동",
							"profileImageUrl": "https://example.com/profile.jpg"
							}
					""")
			)
		)
		@RequestBody SignupRequestDto request,
		HttpServletRequest httpRequest,
		HttpServletResponse response
	) {
		log.info("[SIGNUP] tempToken={}, nickname={}", request.tempToken(), request.nickname());

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
	@Operation(summary = "AccessToken 갱신",
		description = "TokenManager(쿠키)을 사용하여 만료된 AccessToken을 " + "새로 발급받습니다. RefreshToken은 HTTP-Only 쿠키로 자동 전송됩니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "AccessToken 갱신 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TokenResponseDto.class),
				examples = @ExampleObject(
					value = """
						{
						"accessToken": "eyJhbGciOiJIUzI1NiJ9...",
						"memberKey": "google_123456789",
						"userName": null
						}
						"""
				)
			)),
		@ApiResponse(
			responseCode = "400",
			description = "RefreshToken이 없거나 유효하지 않음",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
						{
						"error": "INVALID_REFRESH_TOKEN",
						"message": "RefreshToken이 없거나 만료되었습니다."
						}
						"""
				)
			)),
		@ApiResponse(
			responseCode = "401",
			description = "TokenManager 만료 또는 무효화됨",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
						{
						"error": "TOKEN_EXPIRED",
						"message": "다시 로그인해주세요."
						}
						"""
				)
			))
	})
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

	@AuthenticatedApi(reason = "로그아웃은 로그인한 사용자만 가능합니다")
	@Operation(summary = "로그아웃", description = "현재 디바이스에서 로그아웃합니다. RefreshToken을 DB에서 무효화합니다.")
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

	@AuthenticatedApi(reason = "모든 디바이스 로그아웃은 로그인한 사용자만 가능합니다")
	@Operation(summary = "모든 디바이스 로그아웃", description = "해당 사용자의 모든 디바이스에서 로그아웃합니다. 모든 토큰을 무효화합니다.")
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
	
	@AuthenticatedApi(reason = "회원 탈퇴는 로그인한 사용자만 가능합니다")
	@Operation(
		summary = "회원 탈퇴", 
		description = "현재 로그인한 사용자의 회원 탈퇴를 처리합니다. " +
			"모든 토큰을 무효화하고 계정을 비활성화합니다. " +
			"Soft Delete 방식으로 처리되어 일정 기간 후 완전 삭제됩니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원 탈퇴 성공",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"success": true,
						"message": "회원 탈퇴가 완료되었습니다."
					}
					""")
			)),
		@ApiResponse(
			responseCode = "400",
			description = "이미 탈퇴한 회원",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"success": false,
						"message": "이미 탈퇴한 회원입니다."
					}
					""")
			)),
		@ApiResponse(
			responseCode = "401",
			description = "인증되지 않은 사용자",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
						"error": "UNAUTHORIZED",
						"message": "로그인이 필요합니다."
					}
					""")
			))
	})
	@PostMapping("/withdraw")
	public ResponseEntity<Void> withdraw(HttpServletRequest request, HttpServletResponse response,
		@AuthenticationPrincipal PrincipalDetails currentUser) {
		String memberKey = currentUser.getUsername();
		
		log.info("[WITHDRAW] 회원 탈퇴 요청 - memberKey: {}", memberKey);

		authService.withdrawMember(memberKey);

		WebUtils.removeRefreshTokenCookie(response);
		
		log.info("[WITHDRAW] 회원 탈퇴 완료 - memberKey: {}", memberKey);
		return ResponseEntity.ok().build();
	}
}
