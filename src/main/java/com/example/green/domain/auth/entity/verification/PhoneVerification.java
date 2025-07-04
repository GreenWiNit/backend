package com.example.green.domain.auth.entity.verification;

import java.time.LocalDateTime;

import com.example.green.domain.auth.entity.verification.vo.Attempt;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.entity.verification.vo.VerificationStatus;
import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "phone_verifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class PhoneVerification {

	private static final int TOKEN_LENGTH = 16;
	private static final int EXPIRATION_MINUTES = 10;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private PhoneNumber phoneNumber;
	private String token;
	private Attempt attempt;
	private VerificationStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	private PhoneVerification(PhoneNumber phoneNumber, String token, LocalDateTime createdAt) {
		this.phoneNumber = phoneNumber;
		this.token = token;
		this.attempt = Attempt.init();
		this.status = VerificationStatus.PENDING;
		this.createdAt = createdAt;
		this.expiresAt = createdAt.plusMinutes(EXPIRATION_MINUTES);
	}

	public static PhoneVerification of(PhoneNumber phoneNumber, TokenGenerator tokenGenerator, LocalDateTime now) {
		return new PhoneVerification(phoneNumber, tokenGenerator.generate(TOKEN_LENGTH), now);
	}

	public void verifyExpiration(LocalDateTime now) {
		if (now.isAfter(expiresAt)) {
			throw new AuthException(PhoneExceptionMessage.VERIFICATION_EXPIRED);
		}
	}

	public boolean verifyToken(String token) {
		this.attempt = this.attempt.increaseCount();
		boolean result = this.token.equals(token);
		if (result) {
			this.status = VerificationStatus.VERIFIED;
		}
		return result;
	}

	public void markAsReissue() {
		this.status = VerificationStatus.REISSUED;
	}
}
