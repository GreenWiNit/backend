package com.example.green.domain.auth.utils;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

/**
 * 웹 관련 유틸리티 클래스
 * HTTP 요청/응답 처리, 쿠키 관리, 클라이언트 정보 추출 등
 */
public class WebUtils {

	// HTTP 헤더 관련
	private static final String USER_AGENT_HEADER = "User-Agent";
	private static final String X_FORWARDED_FOR = "X-Forwarded-For";
	private static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
	private static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
	private static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
	private static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

	// 디바이스 관련
	private static final String DEVICE_MOBILE = "Mobile";
	private static final String DEVICE_TABLET = "Tablet";
	private static final String DEVICE_DESKTOP = "Desktop";
	private static final String DEVICE_UNKNOWN = "Unknown";
	private static final String ANDROID = "Android";
	private static final String IPHONE = "iPhone";
	private static final String IPAD = "iPad";

	// 네트워크 관련
	private static final String HTTPS_SCHEME = "https";
	private static final String UNKNOWN_IP = "unknown";
	private static final String IPV6_LOOPBACK = "0:0:0:0:0:0:0:1";
	private static final String IPV4_LOOPBACK = "127.0.0.1";
	private static final String LOCALHOST = "localhost";

	private WebUtils() {
	}

	// 쿠키 관련 유틸리티

	/**
	 * RefreshToken용 HTTP-Only 쿠키 생성
	 */
	public static ResponseCookie createRefreshTokenCookie(String value, boolean secure, int maxAge) {
		return createRefreshTokenResponseCookie(value, secure, maxAge, null);
	}

	/**
	 * RefreshToken용 HTTP-Only 쿠키 생성 (도메인 지정)
	 */
	public static ResponseCookie createRefreshTokenResponseCookie(String value, boolean secure, int maxAge, String domain) {
		ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, value)
			.path("/")
			.httpOnly(true)
			.maxAge(maxAge)
			.secure(secure);
		
		if (domain == null || domain == "localhost") {
			builder.sameSite("None");
		} else {
			builder.domain(domain);
		}
		
		return builder.build();
	}

	/**
	 * TokenManager 쿠키 삭제 (Max-Age=0)
	 */
	public static void removeRefreshTokenCookie(HttpServletResponse response) {
		removeRefreshTokenCookie(response, null, false);
	}

	public static void removeRefreshTokenCookie(HttpServletResponse response, String domain, boolean secure) {
		Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		if (domain != null && !domain.isBlank()) {
			cookie.setDomain(domain);
		}
		cookie.setSecure(secure);
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

	// HTTP 요청 분석 유틸리티

	/**
	 * 요청이 HTTPS인지 판단
	 */
	public static boolean isSecureRequest(HttpServletRequest request) {
		String scheme = request.getScheme();
		return HTTPS_SCHEME.equalsIgnoreCase(scheme);
	}

	/**
	 * User-Agent 기반 디바이스 정보 추출
	 */
	public static String extractDeviceInfo(HttpServletRequest request) {
		String userAgent = request.getHeader(USER_AGENT_HEADER);
		if (userAgent == null) {
			return DEVICE_UNKNOWN;
		}

		// 디바이스 타입별로 검사할 키워드 리스트 정의
		Map<String, List<String>> deviceKeywords = Map.of(
			DEVICE_MOBILE, List.of(DEVICE_MOBILE, ANDROID, IPHONE),
			DEVICE_TABLET, List.of(DEVICE_TABLET, IPAD)
		);

		// 스트림으로 순회하며 첫 번째 매칭되는 타입을 찾고, 없으면 DESKTOP
		return deviceKeywords.entrySet().stream()
			.filter(entry -> entry.getValue().stream().anyMatch(userAgent::contains))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(DEVICE_DESKTOP);
	}

	/**
	 * 클라이언트 실제 IP 주소 추출 (Proxy, Load Balancer 헤더 고려)
	 */
	public static String extractClientIp(HttpServletRequest request) {
		String[] proxyHeaders = {
			X_FORWARDED_FOR, PROXY_CLIENT_IP, WL_PROXY_CLIENT_IP,
			HTTP_CLIENT_IP, HTTP_X_FORWARDED_FOR
		};

		for (String header : proxyHeaders) {
			String ipList = request.getHeader(header);
			if (ipList != null && !ipList.isEmpty() && !UNKNOWN_IP.equalsIgnoreCase(ipList)) {
				// 첫 번째 IP가 실제 클라이언트 IP
				return ipList.split(",")[0].trim();
			}
		}

		String ip = request.getRemoteAddr();
		// IPv6 loopback을 IPv4로 변환
		return IPV6_LOOPBACK.equals(ip) ? IPV4_LOOPBACK : ip;
	}

	// Frontend URL 분석 유틸리티

	/**
	 * Frontend URL이 개발 환경인지 판단
	 */
	public static boolean isLocalDevelopment(String frontendUrl) {
		if (frontendUrl == null) {
			return true;
		}
        try {
            java.net.URI uri = java.net.URI.create(frontendUrl);
            String host = uri.getHost();
            if (host == null) {
                return true;
            }
            String h = host.toLowerCase();
            // Only exact loopback names/addresses are considered local.
            return LOCALHOST.equals(h)
                || IPV4_LOOPBACK.equals(h)
                || "::1".equals(h)
                || IPV6_LOOPBACK.equals(h);
        } catch (Exception e) {
            // URI parsing failed; conservative fallback (exact matches only)
            String s = frontendUrl.toLowerCase();
            return s.equals(LOCALHOST) || s.equals(IPV4_LOOPBACK) || s.equals("::1") || s.equals(IPV6_LOOPBACK);
        }
	}

	public static String toRegistrableDomain(String host) {
		if (host == null) {
			return null;
		}
		String h = host.trim().toLowerCase();
		if (h.equals("localhost") || h.matches("^\\d+\\.\\d+\\.\\d+\\.\\d+$")) {
			return null;
		}
		String[] parts = h.split("\\.");
		if (parts.length < 2) {
			return null;
		}
		// naive eTLD+1 (sufficient for *.store); consider config if you need PSL accuracy.
		return parts[parts.length - 2] + "." + parts[parts.length - 1];
	}
}
