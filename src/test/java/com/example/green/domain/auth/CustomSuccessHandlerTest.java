/*
  Testing stack note:
  - Using JUnit Jupiter (JUnit 5) and Mockito (with MockitoExtension).
  - Using Mockito's static mocking (MockedStatic) for WebUtils, AccessToken, and TempToken.
  - Tests focus on CustomSuccessHandler.onAuthenticationSuccess paths introduced/affected by PR changes.
*/
package com.example.green.domain.auth;

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.OAuth2UserInfoDto;
import com.example.green.domain.auth.entity.vo.AccessToken;
import com.example.green.domain.auth.entity.vo.TempToken;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.auth.utils.WebUtils;
import com.example.green.domain.auth.OAuth2RedirectValidator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class CustomSuccessHandlerTest {

    @Mock
    TokenService tokenService;

    @Mock
    OAuth2RedirectValidator redirectValidator;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Mock
    CustomOAuth2UserDto customUser;

    @Mock
    com.example.green.domain.auth.dto.CustomOAuth2UserDto.UserDto userDto;

    @Mock
    OAuth2UserInfoDto oauth2Info;

    CustomSuccessHandler handler;

    @BeforeEach
    void setUp() {
        // frontendBaseUrl is not used in current logic but is required by constructor
        handler = new CustomSuccessHandler(tokenService, "http://frontend.example.com", redirectValidator);
        // Default authority
        when(authentication.getAuthorities()).thenReturn(List.of((GrantedAuthority) () -> "ROLE_USER"));
        when(authentication.getPrincipal()).thenReturn(customUser);
        when(customUser.getUserDto()).thenReturn(userDto);
    }

    @AfterEach
    void tearDown() {
        // no-op
    }

    @Nested
    @DisplayName("New user flow")
    class NewUserFlow {

        @Test
        @DisplayName("Redirects to signup with encoded temp token when redirect base is valid")
        void redirectsToSignupForNewUserWithValidRedirect() throws Exception {
            when(userDto.isNewUser()).thenReturn(true);
            when(userDto.oauth2UserInfoDto()).thenReturn(oauth2Info);
            when(oauth2Info.email()).thenReturn("john@example.com");
            when(oauth2Info.name()).thenReturn("John Doe");
            when(oauth2Info.profileImageUrl()).thenReturn("http://img");
            when(oauth2Info.provider()).thenReturn("google");
            when(oauth2Info.providerId()).thenReturn("123");

            when(redirectValidator.getSafeRedirectBase(request)).thenReturn("https://app.example.com");
            when(tokenService.createTemporaryToken(any(), any(), any(), any(), any())).thenReturn("temp token with space");

            TempToken tempTokenMock = mock(TempToken.class);
            when(tempTokenMock.getValue()).thenReturn("temp token with space");
            try (MockedStatic<TempToken> tempTokenStatic = Mockito.mockStatic(TempToken.class)) {
                tempTokenStatic.when(() -> TempToken.from(eq("temp token with space"), any())).thenReturn(tempTokenMock);

                handler.onAuthenticationSuccess(request, response, authentication);

                // Expect redirect to /signup with URL-encoded token (space -> +)
                verify(response, times(1)).sendRedirect("https://app.example.com/signup?tempToken=temp+token+with+space");
                verify(response, never()).sendError(anyInt(), anyString());
            }
        }

        @Test
        @DisplayName("Sends 400 error when redirect base is invalid (null)")
        void sendsBadRequestForNewUserWhenRedirectBaseInvalid() throws Exception {
            when(userDto.isNewUser()).thenReturn(true);
            when(userDto.oauth2UserInfoDto()).thenReturn(oauth2Info);

            when(redirectValidator.getSafeRedirectBase(request)).thenReturn(null);
            when(tokenService.createTemporaryToken(any(), any(), any(), any(), any())).thenReturn("any");

            TempToken tempTokenMock = mock(TempToken.class);
            when(tempTokenMock.getValue()).thenReturn("any");
            try (MockedStatic<TempToken> tempTokenStatic = Mockito.mockStatic(TempToken.class)) {
                tempTokenStatic.when(() -> TempToken.from(eq("any"), any())).thenReturn(tempTokenMock);

                handler.onAuthenticationSuccess(request, response, authentication);

                verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
                verify(response, never()).sendRedirect(anyString());
            }
        }
    }

    @Nested
    @DisplayName("Existing user flow")
    class ExistingUserFlow {

        @BeforeEach
        void setupExisting() {
            when(userDto.isNewUser()).thenReturn(false);
            when(customUser.getMemberKey()).thenReturn("member-123");
            when(customUser.getName()).thenReturn("Jane Doe");
        }

        @Test
        @DisplayName("Issues tokens, sets secure cookie with domain and redirects for non-localhost base")
        void existingUserNonLocalhost() throws Exception {
            when(redirectValidator.getSafeRedirectBase(request)).thenReturn("https://app.example.com");

            when(tokenService.createRefreshToken(eq("member-123"), anyString(), anyString())).thenReturn("REFRESH-TOKEN");
            when(tokenService.createAccessToken(eq("member-123"), eq("ROLE_USER"))).thenReturn("ACCESS TOKEN RAW");

            AccessToken accessToken = mock(AccessToken.class);
            when(accessToken.getValue()).thenReturn("ACCESS TOKEN RAW");

            try (MockedStatic<AccessToken> accessTokenStatic = Mockito.mockStatic(AccessToken.class);
                 MockedStatic<WebUtils> webUtilsStatic = Mockito.mockStatic(WebUtils.class)) {

                accessTokenStatic.when(() -> AccessToken.from(eq("ACCESS TOKEN RAW"), any())).thenReturn(accessToken);

                ArgumentCaptor<String> refreshValueCap = ArgumentCaptor.forClass(String.class);
                ArgumentCaptor<Boolean> secureCap = ArgumentCaptor.forClass(Boolean.class);
                ArgumentCaptor<Integer> maxAgeCap = ArgumentCaptor.forClass(Integer.class);
                ArgumentCaptor<String> domainCap = ArgumentCaptor.forClass(String.class);

                Cookie mockCookie = new Cookie("refreshToken", "REFRESH-TOKEN");
                mockCookie.setHttpOnly(true);
                webUtilsStatic.when(() -> WebUtils.createRefreshTokenCookie(refreshValueCap.capture(),
                        secureCap.capture(), maxAgeCap.capture(), domainCap.capture()))
                        .thenReturn(mockCookie);

                handler.onAuthenticationSuccess(request, response, authentication);

                // Verify cookie creation arguments
                assertEquals("REFRESH-TOKEN", refreshValueCap.getValue());
                assertTrue(secureCap.getValue(), "secure flag should be true for non-localhost");
                assertEquals(7 * 24 * 60 * 60, maxAgeCap.getValue());
                assertEquals("app.example.com", domainCap.getValue());

                // ast-grep-ignore: CWE-352
                verify(response).addCookie(mockCookie);
                verify(response).sendRedirect("https://app.example.com/?accessToken=ACCESS+TOKEN+RAW&userName=Jane+Doe");
                verify(response, never()).sendError(anyInt(), anyString());
            }
        }

        @Test
        @DisplayName("Issues tokens, sets non-secure cookie without domain and redirects for localhost base")
        void existingUserLocalhost() throws Exception {
            when(redirectValidator.getSafeRedirectBase(request)).thenReturn("http://localhost:5173");

            when(tokenService.createRefreshToken(eq("member-123"), anyString(), anyString())).thenReturn("REFRESH-TOKEN-LH");
            when(tokenService.createAccessToken(eq("member-123"), eq("ROLE_USER"))).thenReturn("ACCESS-LH");

            AccessToken accessToken = mock(AccessToken.class);
            when(accessToken.getValue()).thenReturn("ACCESS-LH");
            try (MockedStatic<AccessToken> accessTokenStatic = Mockito.mockStatic(AccessToken.class);
                 MockedStatic<WebUtils> webUtilsStatic = Mockito.mockStatic(WebUtils.class)) {

                accessTokenStatic.when(() -> AccessToken.from(eq("ACCESS-LH"), any())).thenReturn(accessToken);

                ArgumentCaptor<String> refreshValueCap = ArgumentCaptor.forClass(String.class);
                ArgumentCaptor<Boolean> secureCap = ArgumentCaptor.forClass(Boolean.class);
                ArgumentCaptor<Integer> maxAgeCap = ArgumentCaptor.forClass(Integer.class);
                ArgumentCaptor<String> domainCap = ArgumentCaptor.forClass(String.class);

                Cookie mockCookie = new Cookie("refreshToken", "REFRESH-TOKEN-LH");
                mockCookie.setHttpOnly(true);
                webUtilsStatic.when(() -> WebUtils.createRefreshTokenCookie(refreshValueCap.capture(),
                        secureCap.capture(), maxAgeCap.capture(), domainCap.capture()))
                        .thenReturn(mockCookie);

                handler.onAuthenticationSuccess(request, response, authentication);

                assertEquals("REFRESH-TOKEN-LH", refreshValueCap.getValue());
                assertFalse(secureCap.getValue(), "secure flag should be false for localhost");
                assertNull(domainCap.getValue(), "domain should be null for localhost");

                // ast-grep-ignore: CWE-352
                verify(response).addCookie(mockCookie);
                verify(response).sendRedirect("http://localhost:5173/?accessToken=ACCESS-LH&userName=Jane+Doe");
            }
        }

        @Test
        @DisplayName("Sends 400 error and does not create tokens when redirect base is invalid (null)")
        void existingUserInvalidRedirect() throws Exception {
            when(redirectValidator.getSafeRedirectBase(request)).thenReturn(null);

            handler.onAuthenticationSuccess(request, response, authentication);

            verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid origin or referer");
            verify(response, never()).sendRedirect(anyString());
            verify(tokenService, never()).createRefreshToken(anyString(), anyString(), anyString());
            verify(tokenService, never()).createAccessToken(anyString(), anyString());
        }

        @Test
        @DisplayName("Continues with null domain when domain extraction fails")
        void existingUserDomainExtractionFailure() throws Exception {
            // Invalid but non-localhost base to trigger secureFlag=true and URI parsing error
            when(redirectValidator.getSafeRedirectBase(request)).thenReturn("https://:invalid");

            when(tokenService.createRefreshToken(eq("member-123"), anyString(), anyString())).thenReturn("R");
            when(tokenService.createAccessToken(eq("member-123"), eq("ROLE_USER"))).thenReturn("A T");

            AccessToken accessToken = mock(AccessToken.class);
            when(accessToken.getValue()).thenReturn("A T");

            try (MockedStatic<AccessToken> accessTokenStatic = Mockito.mockStatic(AccessToken.class);
                 MockedStatic<WebUtils> webUtilsStatic = Mockito.mockStatic(WebUtils.class)) {
                accessTokenStatic.when(() -> AccessToken.from(eq("A T"), any())).thenReturn(accessToken);

                ArgumentCaptor<String> domainCap = ArgumentCaptor.forClass(String.class);
                Cookie mockCookie = new Cookie("refreshToken", "R");
                webUtilsStatic.when(() -> WebUtils.createRefreshTokenCookie(anyString(), anyBoolean(), anyInt(), domainCap.capture()))
                        .thenReturn(mockCookie);

                handler.onAuthenticationSuccess(request, response, authentication);

                assertNull(domainCap.getValue(), "domain should be null when URI parsing fails");
                verify(response).sendRedirect("https://:invalid/?accessToken=A+T&userName=Jane+Doe");
            }
        }
    }

    @Test
    @DisplayName("Throws when authorities are empty")
    void throwsWhenNoAuthorities() {
        when(authentication.getAuthorities()).thenReturn(List.of());
        when(userDto.isNewUser()).thenReturn(false);
        Executable exec = () -> handler.onAuthenticationSuccess(request, response, authentication);
        assertThrows(java.util.NoSuchElementException.class, exec);
    }
}