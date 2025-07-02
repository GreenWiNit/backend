package com.example.green.domain.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 웹 관련 유틸리티 클래스
 * HTTP 요청/응답 처리, 쿠키 관리, 클라이언트 정보 추출 등
 */
public class WebUtils {

	private WebUtils() {
	}

	// ================================
	// 쿠키 관련 유틸리티
	// ================================

	/**
	 * RefreshToken용 HTTP-Only 쿠키 생성
	 */
	public static Cookie createRefreshTokenCookie(String value, boolean secure, int maxAge) {
		Cookie cookie = new Cookie("RefreshToken", value);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(maxAge);
		cookie.setSecure(secure);
		return cookie;
	}

	/**
	 * RefreshToken 쿠키 삭제 (Max-Age=0)
	 */
	public static void removeRefreshTokenCookie(HttpServletResponse response) {
		Cookie cookie = new Cookie("RefreshToken", "");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	/**
	 * 지정한 쿠키명의 값을 HttpServletRequest에서 추출
	 */
	public static String extractCookieValue(HttpServletRequest request, String cookieName) {
		if (request.getCookies() == null) {
			return null;
		}
		for (Cookie cookie : request.getCookies()) {
			if (cookieName.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	// ================================
	// HTTP 요청 분석 유틸리티
	// ================================

	/**
	 * 요청이 HTTPS인지 판단
	 */
	public static boolean isSecureRequest(HttpServletRequest request) {
		String scheme = request.getScheme();
		return "https".equalsIgnoreCase(scheme);
	}

	/**
	 * User-Agent 기반 디바이스 정보 추출
	 */
	public static String extractDeviceInfo(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null) {
			return "Unknown";
		}
		if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
			return "Mobile";
		} else if (userAgent.contains("Tablet") || userAgent.contains("iPad")) {
			return "Tablet";
		}
		return "Desktop";
	}

	/**
	 * 클라이언트 실제 IP 주소 추출 (Proxy, Load Balancer 헤더 고려)
	 */
	public static String extractClientIp(HttpServletRequest request) {
		String[] proxyHeaders = {
			"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
			"HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"
		};

		for (String header : proxyHeaders) {
			String ipList = request.getHeader(header);
			if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
				// 첫 번째 IP가 실제 클라이언트 IP
				return ipList.split(",")[0].trim();
			}
		}

		String ip = request.getRemoteAddr();
		// IPv6 loopback을 IPv4로 변환
		return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
	}

	// ================================
	// Frontend URL 분석 유틸리티
	// ================================

	/**
	 * Frontend URL이 개발 환경인지 판단
	 */
	public static boolean isLocalDevelopment(String frontendUrl) {
		if (frontendUrl == null) {
			return true;
		}
		return frontendUrl.contains("localhost") || frontendUrl.contains("127.0.0.1");
	}
} 