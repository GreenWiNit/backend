package com.example.green.infra.client;

public interface AuthClient {

	/*
		MVP 에서 우선, 전화 인증이 필요 없어진 상태
	 */
	@Deprecated
	boolean isAuthenticated(String phoneNumber);
}
