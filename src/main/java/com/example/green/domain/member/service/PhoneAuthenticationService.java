package com.example.green.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.green.domain.pointshop.delivery.client.PhoneVerificationClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneAuthenticationService {

	private final PhoneVerificationClient phoneVerificationClient;

	public boolean isPhoneAuthenticated(String phoneNumber, Long memberId) {
		if (!isValidPhoneNumber(phoneNumber)) {
			return false;
		}

		try {
			return phoneVerificationClient.isAuthenticated(phoneNumber);
		} catch (Exception e) {
			log.warn("휴대폰 인증 상태 확인 실패: memberId={}, phoneNumber={}, error={}", 
				memberId, phoneNumber, e.getMessage());
			return false;
		}
	}

	private boolean isValidPhoneNumber(String phoneNumber) {
		return StringUtils.hasText(phoneNumber) && !phoneNumber.trim().isEmpty();
	}
} 