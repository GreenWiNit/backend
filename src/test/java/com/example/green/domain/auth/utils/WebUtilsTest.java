package com.example.green.domain.auth.utils;

import com.example.green.domain.auth.constants.AuthConstants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for WebUtils.
 *
 * Testing stack:
 * - JUnit 5 (Jupiter) for unit tests
 * - Mockito for mocking servlet request/response and argument capture
 *
 * These tests focus on the behaviors visible in the provided diff/source.
 */
class WebUtilsTest {

    // Helper to mock a request with scheme
    private static HttpServletRequest mockRequestWithScheme(String scheme) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getScheme()).thenReturn(scheme);
        return req;
    }

    // Helper to mock a request with User-Agent
    private static HttpServletRequest mockRequestWithUserAgent(String ua) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getHeader("User-Agent")).thenReturn(ua);
        return req;
    }

    // Helper to mock a request with headers map
    private static HttpServletRequest mockRequestWithHeaders(java.util.Map<String, String> headers, String remoteAddr) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        for (var e : headers.entrySet()) {
            when(req.getHeader(e.getKey())).thenReturn(e.getValue());
        }
        when(req.getRemoteAddr()).thenReturn(remoteAddr);
        return req;
    }

    @Nested
    @DisplayName("createRefreshTokenCookie(value, secure, maxAge)")
    class CreateRefreshTokenCookieBasicTests {

        @Test
        @DisplayName("Creates HTTP-only, named refresh token cookie with correct attributes")
        void createsHttpOnlyCookie() {
            String value = "refresh-abc";
            boolean secure = true;
            int maxAge = 3600;

            Cookie c = WebUtils.createRefreshTokenCookie(value, secure, maxAge);

            assertNotNull(c);
            assertEquals(AuthConstants.REFRESH_TOKEN_COOKIE_NAME, c.getName());
            assertEquals("/", c.getPath());
            assertTrue(c.isHttpOnly());
            assertEquals(maxAge, c.getMaxAge());
            assertEquals(secure, c.getSecure());
            // Domain not specified -> should be null
            assertNull(c.getDomain(), "Domain should be null when not specified");
            assertEquals(value, c.getValue());
        }

        @Test
        @DisplayName("Handles insecure cookie flag correctly")
        void handlesInsecureFlag() {
            Cookie c = WebUtils.createRefreshTokenCookie("v", false, 10);
            assertFalse(c.getSecure());
        }
    }

    @Nested
    @DisplayName("createRefreshTokenCookie(value, secure, maxAge, domain)")
    class CreateRefreshTokenCookieWithDomainTests {

        @Test
        @DisplayName("Sets domain when non-empty")
        void setsDomainWhenProvided() {
            Cookie c = WebUtils.createRefreshTokenCookie("v", true, 120, "example.com");
            assertEquals("example.com", c.getDomain());
        }

        @Test
        @DisplayName("Does not set domain when null")
        void noDomainWhenNull() {
            Cookie c = WebUtils.createRefreshTokenCookie("v", true, 120, null);
            assertNull(c.getDomain());
        }

        @Test
        @DisplayName("Does not set domain when empty string")
        void noDomainWhenEmpty() {
            Cookie c = WebUtils.createRefreshTokenCookie("v", true, 120, "");
            assertNull(c.getDomain());
        }
    }

    @Nested
    @DisplayName("removeRefreshTokenCookie(response)")
    class RemoveRefreshTokenCookieTests {

        @Test
        @DisplayName("Adds deletion cookie with Max-Age=0 and HttpOnly")
        void addsDeletionCookie() {
            HttpServletResponse resp = mock(HttpServletResponse.class);

            WebUtils.removeRefreshTokenCookie(resp);

            ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
            verify(resp, times(1)).addCookie(captor.capture());

            Cookie c = captor.getValue();
            assertEquals(AuthConstants.REFRESH_TOKEN_COOKIE_NAME, c.getName());
            assertEquals("/", c.getPath());
            assertTrue(c.isHttpOnly());
            assertEquals(0, c.getMaxAge());
            // Value can be empty per implementation
            assertEquals("", c.getValue());
        }
    }

    @Nested
    @DisplayName("extractCookieValue(request, cookieName)")
    class ExtractCookieValueTests {

        @Test
        @DisplayName("Returns null when request has no cookies (null array)")
        void returnsNullWhenNoCookies() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getCookies()).thenReturn(null);

            String v = WebUtils.extractCookieValue(req, "any");
            assertNull(v);
        }

        @Test
        @DisplayName("Returns value of matching cookie")
        void returnsMatchingCookie() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            Cookie cookie1 = new Cookie("a", "1");
            cookie1.setHttpOnly(true);
            Cookie cookie2 = new Cookie("b", "2");
            cookie2.setHttpOnly(true);
            Cookie cookie3 = new Cookie("target", "xyz");
            cookie3.setHttpOnly(true);
            Cookie[] cookies = new Cookie[] { cookie1, cookie2, cookie3 };
            when(req.getCookies()).thenReturn(cookies);

            assertEquals("xyz", WebUtils.extractCookieValue(req, "target"));
        }

        @Test
        @DisplayName("Returns null when no matching cookie is present")
        void returnsNullWhenNoMatch() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            Cookie cookie = new Cookie("a", "1");
            cookie.setHttpOnly(true);
            Cookie[] cookies = new Cookie[] { cookie };
            when(req.getCookies()).thenReturn(cookies);

            assertNull(WebUtils.extractCookieValue(req, "target"));
        }
    }

    @Nested
    @DisplayName("isSecureRequest(request)")
    class IsSecureRequestTests {

        @Test
        @DisplayName("Returns true for https and HTTPS schemes")
        void trueForHttps() {
            assertTrue(WebUtils.isSecureRequest(mockRequestWithScheme("https")));
            assertTrue(WebUtils.isSecureRequest(mockRequestWithScheme("HTTPS")));
        }

        @Test
        @DisplayName("Returns false for http")
        void falseForHttp() {
            assertFalse(WebUtils.isSecureRequest(mockRequestWithScheme("http")));
        }
    }

    @Nested
    @DisplayName("extractDeviceInfo(request)")
    class ExtractDeviceInfoTests {

        @Test
        @DisplayName("Returns 'Unknown' when User-Agent is null")
        void unknownWhenNoUA() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getHeader("User-Agent")).thenReturn(null);
            assertEquals("Unknown", WebUtils.extractDeviceInfo(req));
        }

        @Test
        @DisplayName("Detects Mobile when UA contains Android")
        void detectsMobileAndroid() {
            HttpServletRequest req = mockRequestWithUserAgent("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36");
            assertEquals("Mobile", WebUtils.extractDeviceInfo(req));
        }

        @Test
        @DisplayName("Detects Mobile when UA contains iPhone")
        void detectsMobileIphone() {
            HttpServletRequest req = mockRequestWithUserAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X)");
            assertEquals("Mobile", WebUtils.extractDeviceInfo(req));
        }

        @Test
        @DisplayName("Detects Tablet when UA contains iPad without 'Mobile' token")
        void detectsTabletIpad() {
            // Avoid 'Mobile' token to prevent ambiguous match
            HttpServletRequest req = mockRequestWithUserAgent("Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) Tablet Safari/604.1");
            assertEquals("Tablet", WebUtils.extractDeviceInfo(req));
        }

        @Test
        @DisplayName("Defaults to Desktop when no mobile/tablet keywords present")
        void defaultsToDesktop() {
            HttpServletRequest req = mockRequestWithUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            assertEquals("Desktop", WebUtils.extractDeviceInfo(req));
        }
    }

    @Nested
    @DisplayName("extractClientIp(request)")
    class ExtractClientIpTests {

        @Test
        @DisplayName("Prefers first IP in X-Forwarded-For header")
        void prefersFirstInXForwardedFor() {
            java.util.Map<String, String> headers = new java.util.HashMap<>();
            headers.put("X-Forwarded-For", "203.0.113.1, 70.41.3.18, 150.172.238.178");

            HttpServletRequest req = mockRequestWithHeaders(headers, "192.0.2.55");
            assertEquals("203.0.113.1", WebUtils.extractClientIp(req));
        }

        @Test
        @DisplayName("Skips 'unknown' and uses next proxy header that has a value")
        void skipsUnknownAndUsesNext() {
            java.util.Map<String, String> headers = new java.util.HashMap<>();
            headers.put("X-Forwarded-For", "unknown");
            headers.put("Proxy-Client-IP", "198.51.100.2");

            HttpServletRequest req = mockRequestWithHeaders(headers, "192.0.2.55");
            assertEquals("198.51.100.2", WebUtils.extractClientIp(req));
        }

        @Test
        @DisplayName("Falls back to remote address and maps IPv6 loopback to IPv4")
        void mapsIpv6LoopbackToIpv4() {
            HttpServletRequest req = mockRequestWithHeaders(java.util.Map.of(), "0:0:0:0:0:0:0:1");
            assertEquals("127.0.0.1", WebUtils.extractClientIp(req));
        }

        @Test
        @DisplayName("Falls back to remote address when no proxy headers present")
        void remoteAddrWhenNoHeaders() {
            HttpServletRequest req = mockRequestWithHeaders(java.util.Map.of(), "192.168.1.10");
            assertEquals("192.168.1.10", WebUtils.extractClientIp(req));
        }
    }

    @Nested
    @DisplayName("isLocalDevelopment(frontendUrl)")
    class IsLocalDevelopmentTests {

        @Test
        @DisplayName("Returns true for null")
        void trueForNull() {
            assertTrue(WebUtils.isLocalDevelopment(null));
        }

        @Test
        @DisplayName("Returns true for localhost and 127.0.0.1")
        void trueForLocalhostAndLoopback() {
            assertTrue(WebUtils.isLocalDevelopment("http://localhost:3000"));
            assertTrue(WebUtils.isLocalDevelopment("https://127.0.0.1:8080"));
        }

        @Test
        @DisplayName("Returns false for non-local URLs")
        void falseForNonLocal() {
            assertFalse(WebUtils.isLocalDevelopment("https://example.com/app"));
        }
    }
}

<!-- SKIPPED FIXES:
- ast-grep (CWE-352): CSRF prevention (SameSite flag or CSRF tokens) belongs in implementation code, not in test file.
-->