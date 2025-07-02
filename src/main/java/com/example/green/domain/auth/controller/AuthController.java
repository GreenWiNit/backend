package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.dto.SignupRequestDto;
import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.dto.TokenResponseDto;
import com.example.green.domain.auth.model.vo.AccessToken;
import com.example.green.domain.auth.model.vo.TempToken;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.global.annotation.PublicApi;

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

/**
 * 인증 관련 API 컨트롤러
 */
@Tag(name = "인증 API", description = "OAuth2 로그인, 회원가입, 토큰 관리 등 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	// ================================
	// 쿠키 관련 (이 클래스에서만 사용)
	// ================================
	private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

	private final AuthService authService;
	private final TokenService tokenService;

	@PublicApi
	@Operation(
		summary = "회원가입",
		description = "OAuth2 로그인 후 신규 사용자의 회원가입을 처리합니다. "
			+ "임시 토큰에서 Google 계정 정보를 추출하고, 추가 정보를 받아 회원 등록을 완료합니다."
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
					  "username": "google_123456789",
					  "userName": "홍길동"
					}
					""")
			)
		),
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
					""")
			)
		)
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

		// 1. TempToken VO로 감싸서 타입 안전성 확보
		TempToken tempToken = TempToken.from(request.tempToken(), tokenService);

		// 2. VO 메서드로 안전하게 정보 추출 (내부에서 유효성 검증 수행)
		TempTokenInfoDto tempInfo = tempToken.extractUserInfo();

		// 3. 회원가입 로직 실행
		String username = authService.signup(tempInfo, request.nickname(), request.profileImageUrl());

		// 4. AccessToken VO 생성
		String accessTokenString = tokenService.createAccessToken(username, ROLE_USER);
		AccessToken accessToken = AccessToken.from(accessTokenString, tokenService);

		// 5. RefreshToken 생성 (RefreshToken은 이미 엔티티로 잘 구현되어 있어서 String 유지)
		String refreshTokenString = tokenService.createRefreshToken(
			username,
			WebUtils.extractDeviceInfo(httpRequest),
			WebUtils.extractClientIp(httpRequest)
		);

		// 6. RefreshToken 쿠키 설정
		Cookie cookie = WebUtils.createRefreshTokenCookie(
			refreshTokenString,
			WebUtils.isSecureRequest(httpRequest),
			REFRESH_TOKEN_MAX_AGE
		);
		response.addCookie(cookie);

		log.info("[SIGNUP] completed for username={}", username);
		return ResponseEntity.ok(new TokenResponseDto(
			accessToken.getValue(),    // VO에서 원본 문자열 추출
			username,
			tempToken.getName()        // VO 편의 메서드 활용
		));
	}

	@PublicApi
	@Operation(
		summary = "AccessToken 갱신",
		description = "RefreshToken(쿠키)을 사용하여 만료된 AccessToken을 새로 발급받습니다. " +
			"RefreshToken은 HTTP-Only 쿠키로 자동 전송되며, 새로운 AccessToken(15분 유효)을 응답합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "AccessToken 갱신 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TokenResponseDto.class),
				examples = @ExampleObject(value = """
					{
					  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
					  "username": "google_123456789",
					  "userName": null
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "RefreshToken이 없거나 유효하지 않음",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "error": "INVALID_REFRESH_TOKEN",
					  "message": "RefreshToken이 없거나 만료되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = "RefreshToken 만료 또는 무효화됨",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(value = """
					{
					  "error": "TOKEN_EXPIRED",
					  "message": "다시 로그인해주세요."
					}
					""")
			)
		)
	})
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request) {
		String refreshTokenString = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshTokenString == null) {
			log.warn("[REFRESH] Missing RefreshToken cookie");
			return ResponseEntity.badRequest().build();
		}

		// RefreshToken을 통해 새 AccessToken 생성
		String username = tokenService.getUsername(refreshTokenString);
		String newAccessTokenString = tokenService.refreshAccessToken(refreshTokenString, ROLE_USER);

		// 새 AccessToken을 VO로 감싸기 (타입 안전성)
		AccessToken newAccessToken = AccessToken.from(newAccessTokenString, tokenService);

		log.info("[REFRESH] issued new AccessToken for username={}", username);
		return ResponseEntity.ok(new TokenResponseDto(
			newAccessToken.getValue(),  // VO에서 원본 문자열 추출
			username,
			null
		));
	}

	@PublicApi
	@Operation(
		summary = "로그아웃",
		description = "현재 디바이스에서 로그아웃합니다. RefreshToken을 DB에서 무효화하고 쿠키를 삭제합니다."
	)
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshToken != null) {
			tokenService.revokeRefreshToken(refreshToken);
		}
		WebUtils.removeRefreshTokenCookie(response);
		log.info("[LOGOUT] current device logout completed");
		return ResponseEntity.ok().build();
	}

	@PublicApi
	@Operation(
		summary = "모든 디바이스 로그아웃",
		description = "해당 사용자의 모든 디바이스에서 로그아웃합니다. DB에 저장된 모든 RefreshToken을 무효화합니다."
	)
	@PostMapping("/logout-all")
	public ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response) {
		String refreshToken = WebUtils.extractCookieValue(request, REFRESH_TOKEN_COOKIE_NAME);
		if (refreshToken != null) {
			String username = tokenService.getUsername(refreshToken);
			tokenService.revokeAllRefreshTokens(username);
		}
		WebUtils.removeRefreshTokenCookie(response);
		log.info("[LOGOUT-ALL] all devices logout completed");
		return ResponseEntity.ok().build();
	}

}
