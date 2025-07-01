package com.example.green.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.auth.dto.SignupRequest;
import com.example.green.domain.auth.dto.TokenResponse;
import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
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

@Tag(name = "인증 API", description = "OAuth2 로그인, 회원가입, 토큰 관리 등 인증 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;

	@Operation(
		summary = "회원가입",
		description = "OAuth2 로그인 후 신규 사용자의 회원가입을 처리합니다. " +
			"임시 토큰에서 Google 계정 정보를 추출하고, 추가 정보를 받아 회원 등록을 완료합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "회원가입 성공",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = TokenResponse.class),
				examples = @ExampleObject(
					value = """
						{
						  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
						  "username": "google_123456789",
						  "userName": "홍길동"
						}
						"""
				)
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청 (임시 토큰 만료, 필수 필드 누락 등)",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
						{
						  "error": "INVALID_REQUEST",
						  "message": "임시 토큰이 만료되었습니다."
						}
						"""
				)
			)
		)
	})
	@PublicApi
	@PostMapping("/signup")
	public ResponseEntity<TokenResponse> signup(
		@Parameter(
			description = "회원가입 요청 정보 (임시 토큰, 닉네임, 프로필 이미지 URL)",
			required = true,
			content = @Content(
				schema = @Schema(implementation = SignupRequest.class),
				examples = @ExampleObject(
					value = """
						{
						  "tempToken": "eyJhbGciOiJIUzI1NiJ9...",
						  "nickname": "홍길동",
						  "profileImageUrl": "https://example.com/profile.jpg"
						}
						"""
				)
			)
		)
		@RequestBody SignupRequest request, 
		HttpServletResponse response,
		HttpServletRequest httpRequest) {
		log.info("회원가입 요청: {}", request.tempToken());
		
		// 임시 토큰에서 사용자 정보 추출
		TokenService.TempTokenInfo tempTokenInfo = tokenService.extractTempTokenInfo(request.tempToken());
		
		// 회원가입 처리
		String username = authService.signup(tempTokenInfo, request.nickname(), request.profileImageUrl());
		
		// AccessToken/RefreshToken 생성
		String accessToken = tokenService.createAccessToken(username, "ROLE_USER");
		String refreshToken = tokenService.createRefreshToken(
			username,
			extractDeviceInfo(httpRequest),
			extractClientIp(httpRequest)
		);
		
		// RefreshToken을 HTTP-Only 쿠키에 저장
		response.addCookie(createRefreshTokenCookie(refreshToken));
		
		log.info("회원가입 완료: {}", username);
		return ResponseEntity.ok(new TokenResponse(accessToken, username, tempTokenInfo.getName()));
	}

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
				schema = @Schema(implementation = TokenResponse.class),
				examples = @ExampleObject(
					value = """
						{
						  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
						  "username": "google_123456789",
						  "userName": null
						}
						"""
				)
			)
		),
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
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = "RefreshToken 만료 또는 무효화됨",
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
			)
		)
	})
	@PublicApi
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(
		@Parameter(hidden = true) HttpServletRequest request) {
		// 쿠키에서 RefreshToken 추출
		String refreshToken = extractRefreshTokenFromCookie(request);
		if (refreshToken == null) {
			return ResponseEntity.badRequest().build();
		}
		
		// RefreshToken 검증 및 새 AccessToken 발급
		String username = tokenService.getUsername(refreshToken);
		String newAccessToken = tokenService.refreshAccessToken(refreshToken, "ROLE_USER"); // TODO: 실제 role 조회
		
		log.info("AccessToken 갱신 완료: {}", username);
		return ResponseEntity.ok(new TokenResponse(newAccessToken, username, null));
	}



	@Operation(
		summary = "로그아웃",
		description = "현재 디바이스에서 로그아웃합니다. " +
			"RefreshToken을 DB에서 무효화하고 쿠키를 삭제합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "로그아웃 성공",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
						{
						  "message": "로그아웃이 완료되었습니다."
						}
						"""
				)
			)
		)
	})
	@PublicApi
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(
		@Parameter(hidden = true) HttpServletRequest request, 
		@Parameter(hidden = true) HttpServletResponse response) {
		// 쿠키에서 RefreshToken 추출
		String refreshToken = extractRefreshTokenFromCookie(request);
		if (refreshToken != null) {
			// DB에서 RefreshToken 무효화
			tokenService.revokeRefreshToken(refreshToken);
		}
		
		// RefreshToken 쿠키 삭제
		Cookie cookie = new Cookie("RefreshToken", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		
		log.info("로그아웃 완료");
		return ResponseEntity.ok().build();
	}

	@Operation(
		summary = "모든 디바이스 로그아웃",
		description = "해당 사용자의 모든 디바이스에서 로그아웃합니다. " +
			"DB에 저장된 모든 RefreshToken을 무효화하여 다른 디바이스의 세션도 강제 종료합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "모든 디바이스 로그아웃 성공",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
						{
						  "message": "모든 디바이스에서 로그아웃되었습니다."
						}
						"""
				)
			)
		)
	})
	@PublicApi
	@PostMapping("/logout-all")
	public ResponseEntity<Void> logoutAll(
		@Parameter(hidden = true) HttpServletRequest request, 
		@Parameter(hidden = true) HttpServletResponse response) {
		// 쿠키에서 RefreshToken 추출하여 사용자 확인
		String refreshToken = extractRefreshTokenFromCookie(request);
		if (refreshToken != null) {
			String username = tokenService.getUsername(refreshToken);
			// 해당 사용자의 모든 RefreshToken 무효화
			tokenService.revokeAllRefreshTokens(username);
		}
		
		// RefreshToken 쿠키 삭제
		Cookie cookie = new Cookie("RefreshToken", "");
		cookie.setMaxAge(0);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		
		log.info("모든 디바이스 로그아웃 완료");
		return ResponseEntity.ok().build();
	}

	// 쿠키에서 RefreshToken 추출
	private String extractRefreshTokenFromCookie(HttpServletRequest request) {
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("RefreshToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	// User-Agent에서 디바이스 정보 추출
	private String extractDeviceInfo(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			return "Unknown Device";
		}
		
		// 간단한 디바이스 타입 추출
		if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
			return "Mobile Device";
		} else if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
			return "Tablet";
		} else {
			return "Desktop Browser";
		}
	}

	// 클라이언트 IP 주소 추출
	private String extractClientIp(HttpServletRequest request) {
		// Proxy나 Load Balancer 환경에서의 실제 IP 추출
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		
		// IPv6 로컬 주소를 IPv4로 변환
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		
		return ip;
	}

	// RefreshToken용 쿠키 생성
	private Cookie createRefreshTokenCookie(String refreshToken) {
		Cookie cookie = new Cookie("RefreshToken", refreshToken);
		cookie.setMaxAge(7 * 24 * 60 * 60); // 7일
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		//cookie.setSecure(true); // HTTPS 환경에서만 활성화
		return cookie;
	}
} 