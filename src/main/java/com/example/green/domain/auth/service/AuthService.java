package com.example.green.domain.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.model.entity.RefreshToken;
import com.example.green.domain.auth.repository.RefreshTokenRepository;
import com.example.green.domain.member.service.MemberService;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Auth 도메인 서비스
 * 인증/인가, 토큰 관리, OAuth2 처리, 회원가입 등 인증 관련 모든 비즈니스 로직 처리
 * Member 도메인과의 협력을 통해 인증 플로우 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberService memberService;
	private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * OAuth2 회원가입 처리
	 * TempToken에서 추출한 정보와 사용자 입력 정보를 결합하여 회원가입 처리
	 */
	public String signup(TempTokenInfoDto tempInfo, String nickname, String profileImageUrl) {
		log.info("[AUTH] 회원가입 처리 시작 - provider: {}, email: {}",
			tempInfo.getProvider(), tempInfo.getEmail());

		// Member 도메인에 회원 생성 요청
		String username = memberService.signupFromOAuth2(
			tempInfo.getProvider(),
			tempInfo.getProviderId(),
			tempInfo.getName(),
			tempInfo.getEmail(),
			nickname,
			profileImageUrl
		);

		log.info("[AUTH] 회원가입 완료 - username: {}", username);
		return username;
	}

	/**
	 * 단일 디바이스 로그아웃
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public void logout(String username) {
		Optional<RefreshToken> latestToken = refreshTokenRepository.findLatestByUsernameAndNotRevoked(username);
		if (latestToken.isPresent()) {
			RefreshToken token = latestToken.get();
			Long newTokenVersion = token.logout(); // tokenVersion++
			refreshTokenRepository.save(token);
			log.info("[AUTH] 로그아웃 완료 - AccessToken 무효화: {} (tokenVersion: {})",
				username, newTokenVersion);
		} else {
			log.warn("[AUTH] 로그아웃 대상 토큰을 찾을 수 없음: {}", username);
		}
	}

	/**
	 * 모든 디바이스 로그아웃
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public void logoutAllDevices(String username) {
		List<RefreshToken> allTokens = refreshTokenRepository.findAllByUsernameAndNotRevoked(username);
		if (!allTokens.isEmpty()) {
			// 모든 유효한 RefreshToken의 tokenVersion을 크게 증가
			Long maxTokenVersion = null;
			for (RefreshToken token : allTokens) {
				Long newTokenVersion = token.logoutAllDevices(); // tokenVersion += 1000
				maxTokenVersion = newTokenVersion;
			}
			refreshTokenRepository.saveAll(allTokens);
			log.info("[AUTH] 모든 디바이스 로그아웃 완료 - 모든 AccessToken 무효화: {} (tokenVersion: {})",
				username, maxTokenVersion);
		} else {
			log.warn("[AUTH] 로그아웃 대상 토큰을 찾을 수 없음: {}", username);
		}
	}

}
