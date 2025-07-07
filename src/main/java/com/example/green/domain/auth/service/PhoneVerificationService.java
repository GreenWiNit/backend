package com.example.green.domain.auth.service;

import static com.example.green.domain.auth.entity.verification.vo.VerificationStatus.*;

import java.time.Clock;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.entity.verification.PhoneVerification;
import com.example.green.domain.auth.entity.verification.TokenGenerator;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;
import com.example.green.domain.auth.repository.PhoneVerificationRepository;
import com.example.green.domain.auth.service.result.PhoneVerificationResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PhoneVerificationService {

	private final PhoneVerificationRepository phoneVerificationRepository;
	private final PhoneVerificationEmail phoneVerificationEmail;
	private final TokenGenerator tokenGenerator;
	private final Clock clock;

	public PhoneVerificationResult request(PhoneNumber phoneNumber) {
		phoneVerificationRepository.findByPhoneNumberAndStatus(phoneNumber, PENDING)
			.ifPresent(PhoneVerification::markAsReissue);

		LocalDateTime now = LocalDateTime.now(clock);
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, now);
		phoneVerificationRepository.save(phoneVerification);

		String serverEmail = phoneVerificationEmail.getServerEmail();
		return new PhoneVerificationResult(phoneVerification.getToken(), serverEmail);
	}

	// todo: 통합 테스트
	public void verify(PhoneNumber phoneNumber) {
		PhoneVerification phoneVerification =
			phoneVerificationRepository.findByPhoneNumberAndStatus(phoneNumber, PENDING)
				.orElseThrow(() -> new AuthException(PhoneExceptionMessage.REQUIRES_VERIFY_REQUEST));
		String token = phoneVerificationEmail.extractTokenByPhoneNumber(phoneNumber, phoneVerification.getCreatedAt())
			.orElseThrow(() -> new AuthException(PhoneExceptionMessage.NOT_FOUND_TOKEN));

		phoneVerification.verifyExpiration(LocalDateTime.now(clock));
		phoneVerification.verifyToken(token);
	}

	@Transactional(readOnly = true)
	public boolean isAuthenticated(PhoneNumber phoneNumber) {
		final int validMinutes = 15;
		LocalDateTime validFrom = LocalDateTime.now(clock).minusMinutes(validMinutes);

		return phoneVerificationRepository.existsByPhoneNumberAndStatusAndCreatedAtGreaterThanEqual(
			phoneNumber,
			VERIFIED,
			validFrom
		);
	}
}
