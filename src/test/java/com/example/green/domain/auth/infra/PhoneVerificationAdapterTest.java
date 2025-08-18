package com.example.green.domain.auth.infra;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationService;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationAdapterTest {

	@Mock
	private PhoneVerificationService phoneVerificationService;
	@InjectMocks
	private AuthAdapter phoneVerificationAdapter;

	@Test
	void 인증된_전화번호_정보가_주어지면_Vo로_변환해서_서비스_요청에서_true를_반환한다() {
		// given
		when(phoneVerificationService.isAuthenticated(any(PhoneNumber.class))).thenReturn(true);
		// when
		boolean result = phoneVerificationAdapter.isAuthenticated("010-1234-5678");
		// then
		assertThat(result).isTrue();
	}

	@Test
	void 인증되지_않은_전화번호_정보가_주어지면_Vo로_변환해서_서비스_요청에서_false를_반환한다() {
		// given
		when(phoneVerificationService.isAuthenticated(any(PhoneNumber.class))).thenReturn(false);
		// when
		boolean result = phoneVerificationAdapter.isAuthenticated("010-1234-5678");
		// then
		assertThat(result).isFalse();
	}
}