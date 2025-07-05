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
// todo: 메일 도구로부터 메일 주소 가져오기 + 메일 수신함의 토큰 가져오기
public class PhoneVerificationService {

	private final PhoneVerificationRepository phoneVerificationRepository;
	private final TokenGenerator tokenGenerator;
	private final Clock clock;

	public PhoneVerificationResult request(PhoneNumber phoneNumber) {
		phoneVerificationRepository.findByPhoneNumberAndStatus(phoneNumber, PENDING)
			.ifPresent(PhoneVerification::markAsReissue);

		LocalDateTime now = LocalDateTime.now(clock);
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, now);
		phoneVerificationRepository.save(phoneVerification);

		return new PhoneVerificationResult(phoneVerification.getToken(), "email");
	}

	public void verify(PhoneNumber phoneNumber) {
		phoneVerificationRepository.findByPhoneNumberAndStatus(phoneNumber, PENDING)
			.ifPresentOrElse(phoneVerification -> {
				phoneVerification.verifyExpiration(LocalDateTime.now(clock));
				phoneVerification.verifyToken("token");
			}, () -> {
				throw new AuthException(PhoneExceptionMessage.REQUIRES_VERIFY_REQUEST);
			});
	}
}

