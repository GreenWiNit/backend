package com.example.green.domain.auth.adapter;

import org.springframework.stereotype.Component;

import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationService;
import com.example.green.domain.pointshop.delivery.client.PhoneVerificationClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneVerificationAdapter implements PhoneVerificationClient {

	private final PhoneVerificationService phoneVerificationService;

	@Override
	public boolean isAuthenticated(String phoneNumberValue) {
		PhoneNumber phoneNumber = PhoneNumber.of(phoneNumberValue);
		return phoneVerificationService.isAuthenticated(phoneNumber);
	}
}
