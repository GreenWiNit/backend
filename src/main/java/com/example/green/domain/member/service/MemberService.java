package com.example.green.domain.member.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Member 도메인 서비스
 * 회원 정보 관리, 프로필 관리, 회원 생명주기 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * OAuth2 회원가입 (Member 도메인의 핵심 책임)
	 * Auth 도메인의 TempToken 정보를 받아 회원 생성
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public String signupFromOAuth2(String provider, String providerId, String name, String email,
		String nickname, String profileImageUrl) {
		// OAuth2 정보로 username 생성 (Member 도메인에서 username 생성 규칙 관리)
		String username = provider + " " + providerId;

		return createMember(username, name, email, nickname, profileImageUrl);
	}

	/**
	 * 일반 회원가입 (내부 사용)
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	private String createMember(String username, String name, String email, String nickname, String profileImageUrl) {
		// DB 제약조건 확인: 이미 존재하는 사용자인지 먼저 확인 (가장 효율적)
		if (memberRepository.existsByUsername(username)) {
			log.warn("이미 존재하는 사용자입니다 (DB 체크): {}", username);
			return username;
		}

		try {
			// 새로운 멤버 생성
			Member member = Member.create(username, name, email);

			// 사용자가 입력한 닉네임으로 업데이트 (선택사항)
			if (nickname != null && !nickname.trim().isEmpty()) {
				member.updateNickname(nickname);
				log.info("사용자 지정 닉네임: {}", nickname);
			}

			// 프로필 이미지 URL 업데이트 (선택사항)
			if (profileImageUrl != null && !profileImageUrl.trim().isEmpty()) {
				member.updateProfileImage(profileImageUrl);
				log.info("사용자 지정 프로필 이미지: {}", profileImageUrl);
			}

			memberRepository.save(member);

			log.info("신규 사용자 회원가입 완료: {} ({})", name, email);
			return username;

		} catch (DataIntegrityViolationException e) {
			// DB 제약조건 위반 시 (동시 가입으로 인한 중복)
			log.warn("동시 회원가입으로 인한 중복 감지, 기존 사용자로 처리: {}", username, e);
			return username;
		}
	}

	/**
	 * OAuth2 정보 업데이트 (기존 사용자)
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 100, multiplier = 2.0, random = true)
	)
	public void updateOAuth2Info(String username, String name, String email) {
		memberRepository.findByUsername(username)
			.ifPresent(member -> {
				member.updateOAuth2Info(name, email);
				log.debug("OAuth2 사용자 정보 업데이트 완료: {}", username);
			});
	}

	/**
	 * 사용자 조회 (읽기 전용)
	 */
	@Transactional(readOnly = true)
	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	/**
	 * 사용자 존재 확인
	 */
	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return memberRepository.existsByUsername(username);
	}

}
