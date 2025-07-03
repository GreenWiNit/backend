package com.example.green.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Auth 도메인 서비스
 * 인증/인가, 토큰 관리, OAuth2 처리
 * Member 도메인과의 협력을 통해 인증 관련 비즈니스 로직 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberService memberService;

	/**
	 * 단일 디바이스 로그아웃 (Member 도메인에 위임)
	 */
	public void logout(String username) {
		Long newTokenVersion = memberService.incrementTokenVersion(username);
		if (newTokenVersion != null) {
			log.info("[AUTH] 로그아웃 완료 - AccessToken 무효화: {} (tokenVersion: {})",
				username, newTokenVersion);
		}
	}

	/**
	 * 모든 디바이스 로그아웃 (Member 도메인에 위임)
	 */
	public void logoutAllDevices(String username) {
		Long newTokenVersion = memberService.incrementTokenVersionForAllDevices(username);
		if (newTokenVersion != null) {
			log.info("[AUTH] 모든 디바이스 로그아웃 완료 - 모든 AccessToken 무효화: {} (tokenVersion: {})",
				username, newTokenVersion);
		}
	}

}
