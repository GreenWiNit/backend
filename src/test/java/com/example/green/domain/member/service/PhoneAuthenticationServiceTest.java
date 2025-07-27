package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.pointshop.delivery.client.PhoneVerificationClient;

@ExtendWith(MockitoExtension.class)
class PhoneAuthenticationServiceTest {

	@Mock
	private PhoneVerificationClient phoneVerificationClient;

	@InjectMocks
	private PhoneAuthenticationService phoneAuthenticationService;

	@Test
	void 유효한_휴대폰_번호가_인증되어_있으면_true를_반환한다() {
		// given
		String phoneNumber = "010-1234-5678";
		Long memberId = 1L;
		when(phoneVerificationClient.isAuthenticated(phoneNumber)).thenReturn(true);

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isTrue();
		verify(phoneVerificationClient).isAuthenticated(phoneNumber);
	}

	@Test
	void 유효한_휴대폰_번호가_인증되어_있지_않으면_false를_반환한다() {
		// given
		String phoneNumber = "010-1234-5678";
		Long memberId = 1L;
		when(phoneVerificationClient.isAuthenticated(phoneNumber)).thenReturn(false);

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isFalse();
		verify(phoneVerificationClient).isAuthenticated(phoneNumber);
	}

	@Test
	void null_휴대폰_번호는_false를_반환한다() {
		// given
		String phoneNumber = null;
		Long memberId = 1L;

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isFalse();
		verify(phoneVerificationClient, never()).isAuthenticated(anyString());
	}

	@Test
	void 빈_문자열_휴대폰_번호는_false를_반환한다() {
		// given
		String phoneNumber = "";
		Long memberId = 1L;

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isFalse();
		verify(phoneVerificationClient, never()).isAuthenticated(anyString());
	}

	@Test
	void 공백만_있는_휴대폰_번호는_false를_반환한다() {
		// given
		String phoneNumber = "   ";
		Long memberId = 1L;

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isFalse();
		verify(phoneVerificationClient, never()).isAuthenticated(anyString());
	}

	@Test
	void 인증_확인_중_예외가_발생하면_false를_반환한다() {
		// given
		String phoneNumber = "010-1234-5678";
		Long memberId = 1L;
		when(phoneVerificationClient.isAuthenticated(phoneNumber))
			.thenThrow(new RuntimeException("인증 서비스 오류"));

		// when
		boolean result = phoneAuthenticationService.isPhoneAuthenticated(phoneNumber, memberId);

		// then
		assertThat(result).isFalse();
		verify(phoneVerificationClient).isAuthenticated(phoneNumber);
	}
} 