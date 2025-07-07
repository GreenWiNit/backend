package com.example.green.domain.auth.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.green.domain.auth.entity.verification.PhoneVerification;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.entity.verification.vo.VerificationStatus;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {
	Optional<PhoneVerification> findByPhoneNumberAndStatus(PhoneNumber phoneNumber, VerificationStatus status);

	boolean existsByPhoneNumberAndStatusAndCreatedAtGreaterThanEqual(
		PhoneNumber phoneNumber,
		VerificationStatus status,
		LocalDateTime validFrom
	);
}
