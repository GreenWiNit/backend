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
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.entity.Member;
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
		tokenManager1 = mock(TokenManager.class);
		tokenManager2 = mock(TokenManager.class);
		when(tokenManager1.logoutAllDevices()).thenReturn(1001L);
		when(tokenManager2.logoutAllDevices()).thenReturn(1002L);
	}

	@Test
	@DisplayName("회원 탈퇴 시 모든 토큰 무효화 후 Member 도메인 탈퇴 처리")
	void withdrawMember_ShouldInvalidateAllTokensAndWithdrawMember() {
		// given
		String username = "google 123";
		List<TokenManager> allTokens = Arrays.asList(tokenManager1, tokenManager2);
		
		when(refreshTokenRepository.findAllByUsernameAndNotRevoked(username))
			.thenReturn(allTokens);

		// when
		authService.withdrawMember(username);

		// then
		// 1. 모든 디바이스 로그아웃 확인
		verify(tokenManager1).logoutAllDevices();
		verify(tokenManager2).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		
		// 2. 모든 RefreshToken 무효화 확인
		verify(refreshTokenRepository).revokeAllByUsername(username);
		
		// 3. Member 도메인 탈퇴 처리 확인
		verify(memberService).withdrawMemberByUsername(username);
	}

	@Test
	@DisplayName("토큰이 없는 회원 탈퇴 시에도 정상 처리")
	void withdrawMember_WithNoTokens_ShouldStillWithdrawMember() {
		// given
		String username = "google 123";
		
		when(refreshTokenRepository.findAllByUsernameAndNotRevoked(username))
			.thenReturn(List.of());

		// when
		authService.withdrawMember(username);

		// then
		// 1. 토큰 무효화 단계는 스킵됨
		verify(refreshTokenRepository, never()).saveAll(any());
		
		// 2. RefreshToken 무효화는 실행됨
		verify(refreshTokenRepository).revokeAllByUsername(username);
		
		// 3. Member 도메인 탈퇴 처리는 실행됨
		verify(memberService).withdrawMemberByUsername(username);
	}

	@Test
	@DisplayName("Member 도메인 탈퇴 실패 시 예외 전파")
	void withdrawMember_WhenMemberServiceFails_ShouldPropagateException() {
		// given
		String username = "google 123";
		List<TokenManager> allTokens = Arrays.asList(tokenManager1);
		
		when(refreshTokenRepository.findAllByUsernameAndNotRevoked(username))
			.thenReturn(allTokens);
		
		// Member 도메인에서 탈퇴 실패 시뮬레이션
		doThrow(new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND))
			.when(memberService).withdrawMemberByUsername(username);

		// when & then
		assertThatThrownBy(() -> authService.withdrawMember(username))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
		
		// 토큰 무효화는 완료되었어야 함
		verify(tokenManager1).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		verify(refreshTokenRepository).revokeAllByUsername(username);
		
		// Member 도메인 탈퇴 처리는 호출되었어야 함
		verify(memberService).withdrawMemberByUsername(username);
	}

	@Test
	@DisplayName("토큰 무효화 중 예외 발생 시 예외 전파")
	void withdrawMember_WhenTokenInvalidationFails_ShouldPropagateException() {
		// given
		String username = "google 123";
		List<TokenManager> allTokens = Arrays.asList(tokenManager1);
		
		when(refreshTokenRepository.findAllByUsernameAndNotRevoked(username))
			.thenReturn(allTokens);
		
		// RefreshToken 무효화 중 예외 발생 시뮬레이션
		doThrow(new RuntimeException("Database error"))
			.when(refreshTokenRepository).revokeAllByUsername(username);

		// when & then
		assertThatThrownBy(() -> authService.withdrawMember(username))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("Database error");
		
		// 토큰 로그아웃은 완료되었어야 함
		verify(tokenManager1).logoutAllDevices();
		verify(refreshTokenRepository).saveAll(allTokens);
		
		// Member 도메인 탈퇴 처리는 호출되지 않았어야 함
		verify(memberService, never()).withdrawMemberByUsername(username);
	}
} 