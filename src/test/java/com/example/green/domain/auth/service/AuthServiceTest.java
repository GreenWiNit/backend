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
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private MemberService memberService;

	@InjectMocks
	private AuthService authService;

	private TokenManager tokenManager1;
	private TokenManager tokenManager2;

	@BeforeEach
	void setUp() {
		// 단순히 mock 객체만 생성
		tokenManager1 = mock(TokenManager.class);
		tokenManager2 = mock(TokenManager.class);
	}

	@Test
	@DisplayName("회원 탈퇴 시 모든 토큰 무효화 후 Member 도메인 탈퇴 처리")
	void withdrawMember_ShouldInvalidateAllTokensAndWithdrawMember() {
		// given
		String memberKey = "google 123";
		List<TokenManager> allTokens = Arrays.asList(tokenManager1, tokenManager2);
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(tokenManager2.logoutAllDevices()).thenReturn(1002L);
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(allTokens);

		// when
		authService.withdrawMember(memberKey);

		// then
		verify(tokenManager1).logoutAllDevices();
		verify(tokenManager2).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		verify(refreshTokenRepository).revokeAllByMemberKey(memberKey);
		verify(memberService).withdrawMemberByMemberKey(memberKey);
	}

	@Test
	@DisplayName("토큰 무효화만 수행 시 회원 탈퇴 처리는 하지 않음")
	void invalidateAllTokens_ShouldOnlyInvalidateTokens() {
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
		verify(memberService, never()).withdrawMemberByMemberKey(any());
	}

	@Test
	@DisplayName("토큰이 없는 회원 탈퇴 시에도 정상 처리")
	void withdrawMember_WithNoTokens_ShouldStillWithdrawMember() {
		// given
		String memberKey = "google 123";
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(List.of());

		// when
		authService.withdrawMember(memberKey);

		// then
		verify(refreshTokenRepository, never()).saveAll(any());
		verify(refreshTokenRepository).revokeAllByMemberKey(memberKey);
		verify(memberService).withdrawMemberByMemberKey(memberKey);
	}

	@Test
	@DisplayName("Member 도메인 탈퇴 실패 시 예외 전파")
	void withdrawMember_WhenMemberServiceFails_ShouldPropagateException() {
		// given
		String memberKey = "google 123";
		List<TokenManager> allTokens = List.of(tokenManager1);
		// 이 테스트에서만 사용하는 stubbing
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(allTokens);
		doThrow(new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND))
			.when(memberService).withdrawMemberByMemberKey(memberKey);

		// when & then
		assertThatThrownBy(() -> authService.withdrawMember(memberKey))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());

		verify(tokenManager1).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		verify(refreshTokenRepository).revokeAllByMemberKey(memberKey);
		verify(memberService).withdrawMemberByMemberKey(memberKey);
	}

	@Test
	@DisplayName("토큰 무효화 중 예외 발생 시 예외 전파")
	void withdrawMember_WhenTokenInvalidationFails_ShouldPropagateException() {
		// given
		String memberKey = "google 123";
		List<TokenManager> allTokens = List.of(tokenManager1);
		// 이 테스트에서만 사용하는 stubbing
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey))
			.thenReturn(allTokens);
		doThrow(new RuntimeException("Database error"))
			.when(refreshTokenRepository).revokeAllByMemberKey(memberKey);

		// when & then
		assertThatThrownBy(() -> authService.withdrawMember(memberKey))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Database error");

		verify(tokenManager1).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		verify(memberService, never()).withdrawMemberByMemberKey(memberKey);
	}
}