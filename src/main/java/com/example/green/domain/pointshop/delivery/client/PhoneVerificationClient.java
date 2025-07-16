package com.example.green.domain.pointshop.delivery.client;

public interface PhoneVerificationClient {

	/*
		MVP 에서 우선, 전화 인증이 필요 없어진 상태
	 */
	@Deprecated
	boolean isAuthenticated(String phoneNumber);
}
