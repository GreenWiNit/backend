package com.example.green.domain.auth.service;

import java.util.List;
import java.util.Optional;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.auth.entity.TokenManager;
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
	 * - TempToken에서 추출한 정보와 사용자 입력 정보를 결합
	 * - Member 도메인에 회원 생성 위임
	 * - 회원가입 결과로 생성된 username 반환
	 *
	 * @param tempInfo OAuth2 인증 정보 (provider, providerId, name, email)
	 * @param nickname 사용자가 입력한 닉네임
	 * @param profileImageUrl 사용자가 선택한 프로필 이미지 URL
	 * @return 생성된 사용자의 username
	 */
	public String signup(TempTokenInfoDto tempInfo, String nickname, String profileImageUrl) {
		log.info("[AUTH] 회원가입 처리 시작 - provider: {}, email: {}",
			tempInfo.getProvider(), tempInfo.getEmail());

		String username = createMemberFromOAuth2(tempInfo, nickname, profileImageUrl);

		log.info("[AUTH] 회원가입 완료 - username: {}", username);
		return username;
	}

	/**
	 * OAuth2 정보로 회원 생성
	 * - Member 도메인에 회원 생성 요청
	 *
	 * @param tempInfo OAuth2 인증 정보
	 * @param nickname 닉네임
	 * @param profileImageUrl 프로필 이미지 URL
	 * @return 생성된 사용자의 username
	 */
	private String createMemberFromOAuth2(TempTokenInfoDto tempInfo, String nickname, String profileImageUrl) {
		return memberService.signupFromOAuth2(
			tempInfo.getProvider(),
			tempInfo.getProviderId(),
			tempInfo.getName(),
			tempInfo.getEmail(),
			nickname,
			profileImageUrl
		);
	}

	/**
	 * 단일 디바이스 로그아웃
	 * - 현재 디바이스의 토큰만 무효화
	 * - 토큰이 없으면 경고 로그만 남기고 종료
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public void logout(String memberKey) {
		Optional<TokenManager> latestToken =
			refreshTokenRepository.findFirstByMemberMemberKeyAndIsRevokedFalseOrderByTokenVersionDescIdDesc(memberKey);
		if (latestToken.isPresent()) {
			TokenManager token = latestToken.get();
			Long newTokenVersion = token.logout(); // tokenVersion++
			refreshTokenRepository.save(token);
			log.info("[AUTH] 로그아웃 완료 - AccessToken 무효화: {} (tokenVersion: {})",
				memberKey, newTokenVersion);
		} else {
			log.warn("[AUTH] 로그아웃 대상 토큰을 찾을 수 없음: {}", memberKey);
		}
	}

	/**
	 * 모든 디바이스 로그아웃
	 * - 사용자의 모든 디바이스에서 발급된 토큰 무효화
	 * - 토큰 버전을 크게 증가시켜 모든 AccessToken 무효화
	 * - 토큰이 없으면 경고 로그만 남기고 종료
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public void logoutAllDevices(String memberKey) {
		List<TokenManager> allTokens = refreshTokenRepository.findAllByMemberKeyAndNotRevoked(memberKey);

		if (allTokens.isEmpty()) {
			log.warn("[AUTH] 로그아웃 대상 토큰을 찾을 수 없음: {}", memberKey);
			return;
		}

		// 모든 유효한 RefreshToken의 tokenVersion을 크게 증가
		Long maxTokenVersion = invalidateAllTokens(allTokens);

		log.info("[AUTH] 모든 디바이스 로그아웃 완료 - 모든 AccessToken 무효화: {} (tokenVersion: {})",
			memberKey, maxTokenVersion);
	}

	/**
	 * 모든 토큰 무효화 처리
	 *
	 * @param tokens 무효화할 토큰 목록
	 * @return 최종 토큰 버전
	 */
	private Long invalidateAllTokens(List<TokenManager> tokens) {
		Long maxTokenVersion = null;

		for (TokenManager token : tokens) {
			Long newTokenVersion = token.logoutAllDevices(); // tokenVersion += 1000
			maxTokenVersion = newTokenVersion;
		}

		refreshTokenRepository.saveAll(tokens);
		return maxTokenVersion;
	}

	/**
	 * 토큰 무효화 처리
	 * - 모든 디바이스에서 로그아웃 (AccessToken 무효화)
	 * - 모든 RefreshToken 무효화 (즉시 처리)
	 * - Auth 도메인의 단일 책임: 토큰 관리만 담당
	 *
	 * @param memberKey 회원키
	 */
	public void invalidateAllTokens(String memberKey) {
		log.info("[AUTH] 토큰 무효화 처리 시작 - memberKey: {}", memberKey);

		invalidateAllAuthentications(memberKey);

		log.info("[AUTH] 토큰 무효화 완료 - memberKey: {}", memberKey);

		// TODO: 배치 시스템으로 토큰 물리적 삭제 처리
		// - 탈퇴한 사용자의 모든 TokenManager 레코드 물리적 삭제
		// - 관련 인증 로그, 세션 기록 등도 함께 정리
		// - 개인정보 보호법에 따른 데이터 보관/삭제 정책 적용
	}

	/**
	 * 모든 인증 정보 무효화
	 * - 모든 디바이스 로그아웃 (AccessToken 무효화)
	 * - 모든 RefreshToken 무효화
	 *
	 * @param memberKey 회원키
	 */
	private void invalidateAllAuthentications(String memberKey) {
		// 모든 디바이스에서 로그아웃 (모든 AccessToken 무효화)
		logoutAllDevices(memberKey);

		// 모든 RefreshToken 무효화
		refreshTokenRepository.revokeAllByMemberKey(memberKey);
		log.info("[AUTH] 모든 RefreshToken 무효화 완료: {}", memberKey);
	}

}
