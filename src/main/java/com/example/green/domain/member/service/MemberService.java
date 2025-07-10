package com.example.green.domain.member.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final MemberRepository memberRepository;
	private final FileManager fileManager;

	/**
	 * OAuth2 회원가입
	 * Auth 도메인의 TempToken 정보를 받아 회원 생성
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public String signupFromOAuth2(String provider, String providerId, String name, String email,
		String nickname, String profileImageUrl) {
		// OAuth2 정보로 username 생성
		String username = provider + " " + providerId;

		return createMember(username, name, email, nickname, profileImageUrl);
	}

	/**
	 * 일반 회원가입
	 */
	private String createMember(String username, String name, String email, String nickname, String profileImageUrl) {
		if (memberRepository.existsByUsername(username)) {
			log.warn("이미 존재하는 사용자입니다 (DB 체크): {}", username);
			return username;
		}

		try {
			Member member = Member.create(username, name, email);

			if ((nickname != null && !nickname.trim().isEmpty()) || 
				(profileImageUrl != null && !profileImageUrl.trim().isEmpty())) {
				
				member.updateProfile(nickname, profileImageUrl);
				log.info("사용자 지정 프로필 설정: nickname={}, profileImageUrl={}", nickname, profileImageUrl);
			}
			memberRepository.save(member);

			log.info("신규 사용자 회원가입 완료: {} ({})", name, email);
			return username;

		} catch (DataIntegrityViolationException e) {
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
	 * 활성 회원만 조회 (탈퇴하지 않은 회원)
	 * 토큰 검증 등에서 사용되며, 탈퇴한 회원의 토큰을 자동으로 무효화
	 */
	@Transactional(readOnly = true)
	public Optional<Member> findActiveByUsername(String username) {
		return memberRepository.findActiveByUsername(username);
	}
	
	/**
	 * 활성 회원 존재 여부 확인
	 */
	@Transactional(readOnly = true)
	public boolean existsActiveByUsername(String username) {
		return memberRepository.existsActiveByUsername(username);
	}

	@Transactional
	public Member updateProfile(Long memberId, String nickname, String profileImageUrl) {
		Member member = findMemberById(memberId);
		
		String oldProfileImageUrl = member.getProfile().getProfileImageUrl();

		member.updateProfile(nickname, profileImageUrl);

		processFileManagement(oldProfileImageUrl, profileImageUrl);
		
		log.info("프로필 업데이트 완료: memberId={}, nickname={}, profileImageUrl={}", 
			memberId, nickname, profileImageUrl);
		
		return member;
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.error("프로필 업데이트 실패: 사용자를 찾을 수 없음 - memberId: {}", memberId);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
	}

	private void processFileManagement(String oldProfileImageUrl, String newProfileImageUrl) {

		if (StringUtils.hasText(newProfileImageUrl)) {
			fileManager.confirmUsingImage(newProfileImageUrl);
			log.info("새 프로필 이미지 사용 확정: {}", newProfileImageUrl);
		}

		if (StringUtils.hasText(oldProfileImageUrl)
			&& !oldProfileImageUrl.equals(newProfileImageUrl)) {
			fileManager.unUseImage(oldProfileImageUrl);
			log.info("기존 프로필 이미지 사용 중지: {}", oldProfileImageUrl);
		}

	}

	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 100, multiplier = 2.0, random = true)
	)
	public void withdrawMember(Long memberId) {
		Member member = findMemberById(memberId);

		if (member.isWithdrawn()) {
			log.warn("이미 탈퇴한 회원입니다: memberId={}", memberId);
			throw new BusinessException(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN);
		}

		if (member.getProfile().hasProfileImage()) {
			String profileImageUrl = member.getProfile().getProfileImageUrl();
			fileManager.unUseImage(profileImageUrl);
		}
		member.withdraw();
		
		log.info("회원 탈퇴 완료 (Soft Delete): memberId={}, username={}", 
			memberId, member.getUsername());
			
		// TODO: 배치 시스템으로 물리적 삭제 처리
		// - 탈퇴 후 일정 기간 경과 시 개인정보 완전 삭제
		// - 관련 토큰, 포인트 내역, 주문 내역 등도 함께 처리
		// - 법적 보관 의무가 있는 데이터는 별도 보관소로 이관
	}

	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 100, multiplier = 2.0, random = true)
	)
	public void withdrawMemberByUsername(String username) {
		Member member = memberRepository.findByUsername(username)
			.orElseThrow(() -> {
				log.error("회원 탈퇴 실패: 사용자를 찾을 수 없음 - username: {}", username);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
			
		withdrawMember(member.getId());
	}

}
