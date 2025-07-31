package com.example.green.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.auth.entity.TokenManager;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private MemberService memberService;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@InjectMocks
	private AuthService authService;

	private TokenManager tokenManager1;
	private TokenManager tokenManager2;

	@BeforeEach
	void setUp() {
		tokenManager1 = mock(TokenManager.class);
		tokenManager2 = mock(TokenManager.class);
	}

	@Test
	@DisplayName("토큰 무효화 - 모든 토큰을 무효화함")
	void invalidateAllTokens_ShouldInvalidateAllTokens() {
		// given
		String memberKey = "google 123";
		List<TokenManager> allTokens = Arrays.asList(tokenManager1, tokenManager2);
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(tokenManager2.logoutAllDevices()).thenReturn(1002L);
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(allTokens);

		// when
		authService.invalidateAllTokens(memberKey);

		// then
		verify(tokenManager1).logoutAllDevices();
		verify(tokenManager2).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		verify(refreshTokenRepository).revokeAllByMemberKey(memberKey);
	}

	@Test
	@DisplayName("토큰이 없는 경우에도 정상적으로 무효화 처리")
	void invalidateAllTokens_WithNoTokens_ShouldHandleGracefully() {
		// given
		String memberKey = "google 123";
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(List.of());

		// when
		authService.invalidateAllTokens(memberKey);

		// then
		verify(refreshTokenRepository, never()).saveAll(any());
		verify(refreshTokenRepository).revokeAllByMemberKey(memberKey);
	}

	@Test
	@DisplayName("토큰 무효화 중 예외 발생 시 예외 전파")
	void invalidateAllTokens_WhenTokenInvalidationFails_ShouldPropagateException() {
		// given
		String memberKey = "google 123";
		List<TokenManager> allTokens = List.of(tokenManager1);
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(allTokens);
		doThrow(new RuntimeException("Database error"))
			.when(refreshTokenRepository).revokeAllByMemberKey(memberKey);

		// when & then
		assertThatThrownBy(() -> authService.invalidateAllTokens(memberKey))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Database error");

		verify(tokenManager1).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
	}
}