package com.example.green.domain.auth.infra;

import org.springframework.stereotype.Component;

import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationService;
import com.example.green.infra.client.AuthClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthAdapter implements AuthClient {

	private final PhoneVerificationService phoneVerificationService;

	@Override
	public boolean isAuthenticated(String phoneNumberValue) {
		PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberValue);
		return phoneVerificationService.isAuthenticated(phoneNumber);
	}
}
