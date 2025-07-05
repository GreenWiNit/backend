package com.example.green.domain.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;

public interface PhoneVerificationEmail {

	String getServerEmail();

	Optional<String> extractTokenByPhoneNumber(PhoneNumber phoneNumber, LocalDateTime since);
}
