package com.example.green.domain.auth.service;

import static com.example.green.domain.auth.entity.verification.vo.VerificationStatus.*;
import static com.example.green.domain.auth.exception.PhoneExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.auth.entity.verification.PhoneVerification;
import com.example.green.domain.auth.entity.verification.TokenGenerator;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.repository.PhoneVerificationRepository;
import com.example.green.domain.auth.service.result.PhoneVerificationResult;

@ExtendWith(MockitoExtension.class)
class PhoneVerificationServiceTest {

	@Mock
	private PhoneVerificationRepository phoneVerificationRepository;
	@Mock
	private TokenGenerator tokenGenerator;
	@Mock
	private Clock clock;
	@Mock
	private PhoneVerificationEmail phoneVerificationEmail;
	@InjectMocks
	private PhoneVerificationService phoneVerificationService;

	@Test
	void 주어진_전화번호로_인증_요청_기록이_있으면_재요청_처리하고_새로_발급한다() {
		// given
		when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		when(clock.instant()).thenReturn(Instant.parse("2025-07-05T10:00:00Z"));
		when(tokenGenerator.generate(anyInt())).thenReturn("token");
		when(phoneVerificationEmail.getServerEmail()).thenReturn("email");

		PhoneVerification presentedEntity = mock(PhoneVerification.class);
		when(phoneVerificationRepository.findByPhoneNumberAndStatus(any(PhoneNumber.class), eq(PENDING)))
			.thenReturn(Optional.of(presentedEntity));

		// when
		PhoneVerificationResult result = phoneVerificationService.request(PhoneNumber.of("010-1234-5678"));

		// then
		assertThat(result.serverEmailAddress()).isEqualTo("email");
		assertThat(result.token()).isEqualTo("token");
		verify(presentedEntity).markAsReissue();
		verify(phoneVerificationRepository).save(any(PhoneVerification.class));
	}

	@Test
	void 주어진_전화번호로_인증_요청_기록이_없으면_새로_발급한다() {
		// given
		when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		when(clock.instant()).thenReturn(Instant.parse("2025-07-05T10:00:00Z"));
		when(tokenGenerator.generate(anyInt())).thenReturn("token");
		when(phoneVerificationEmail.getServerEmail()).thenReturn("email");
		when(phoneVerificationRepository.findByPhoneNumberAndStatus(any(PhoneNumber.class), eq(PENDING)))
			.thenReturn(Optional.empty());

		// when
		PhoneVerificationResult result = phoneVerificationService.request(PhoneNumber.of("010-1234-5678"));

		// then
		assertThat(result.serverEmailAddress()).isEqualTo("email");
		assertThat(result.token()).isEqualTo("token");
		verify(phoneVerificationRepository).save(any(PhoneVerification.class));
	}

	@Test
	void 주어진_전화번호가_인증_요청을_기다리는_상태면_시간_검증과_토큰_검증을_한다() {
		// given
		when(clock.getZone()).thenReturn(ZoneId.systemDefault());
		when(clock.instant()).thenReturn(Instant.parse("2025-07-05T10:00:00Z"));
		PhoneVerification presentedEntity = mock(PhoneVerification.class);
		when(presentedEntity.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(phoneVerificationRepository.findByPhoneNumberAndStatus(any(PhoneNumber.class), eq(PENDING)))
			.thenReturn(Optional.of(presentedEntity));
		when(phoneVerificationEmail.extractTokenByPhoneNumber(any(PhoneNumber.class), any(LocalDateTime.class)))
			.thenReturn(Optional.of("token"));

		// when
		phoneVerificationService.verify(PhoneNumber.of("010-1234-5678"));

		// then
		verify(presentedEntity).verifyExpiration(any(LocalDateTime.class));
		verify(presentedEntity).verifyToken(anyString());
	}

	@Test
	void 주어진_전화번호로_인증_요청이_없다면_예외가_발생한다() {
		// given
		when(phoneVerificationRepository.findByPhoneNumberAndStatus(any(PhoneNumber.class), eq(PENDING)))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> phoneVerificationService.verify(PhoneNumber.of("010-1234-5678")))
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", REQUIRES_VERIFY_REQUEST);
	}

	@Test
	void 주어진_전화번호로_메일에_토큰_정보가_없다면_예외가_발생한다() {
		// given
		PhoneVerification presentedEntity = mock(PhoneVerification.class);
		when(presentedEntity.getCreatedAt()).thenReturn(LocalDateTime.now());
		when(phoneVerificationRepository.findByPhoneNumberAndStatus(any(PhoneNumber.class), eq(PENDING)))
			.thenReturn(Optional.of(presentedEntity));
		when(phoneVerificationEmail.extractTokenByPhoneNumber(any(PhoneNumber.class), any(LocalDateTime.class)))
			.thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> phoneVerificationService.verify(PhoneNumber.of("010-1234-5678")))
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", NOT_FOUND_TOKEN);
	}
}