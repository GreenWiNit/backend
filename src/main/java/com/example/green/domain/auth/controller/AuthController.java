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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;

	// 회원가입 API
	@PublicApi
	@PostMapping("/signup")
	public ResponseEntity<TokenResponse> signup(@RequestBody SignupRequest request, HttpServletResponse response,
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

	// AccessToken 갱신 API
	@PublicApi
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refreshToken(HttpServletRequest request) {
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

	// 쿠키 → 헤더 방식 전환 API (OAuth2 완료 후 사용)
	@PublicApi
	@PostMapping("/convert-token")
	public ResponseEntity<TokenResponse> convertCookieToHeader(HttpServletRequest request) {
		// 쿠키에서 RefreshToken 추출
		String refreshToken = extractRefreshTokenFromCookie(request);
		if (refreshToken == null) {
			return ResponseEntity.badRequest().build();
		}
		
		// 새로운 AccessToken/RefreshToken 발급
		String username = tokenService.getUsername(refreshToken);
		String newAccessToken = tokenService.createAccessToken(username, "ROLE_USER"); // TODO: 실제 role 조회
		String newRefreshToken = tokenService.createRefreshToken(
			username,
			extractDeviceInfo(request),
			extractClientIp(request)
		);
		
		log.info("쿠키 → 헤더 방식 전환 완료: {}", username);
		return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken, username, null));
	}

	// 로그아웃 API
	@PublicApi
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
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

	// 모든 디바이스 로그아웃 API
	@PublicApi
	@PostMapping("/logout-all")
	public ResponseEntity<Void> logoutAll(HttpServletRequest request, HttpServletResponse response) {
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