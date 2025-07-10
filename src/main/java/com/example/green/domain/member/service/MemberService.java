package com.example.green.domain.member.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;

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
	private final FileManager fileManager;

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

	@Transactional(readOnly = true)
	public Optional<Member> findByUsername(String username) {
		return memberRepository.findByUsername(username);
	}

	@Transactional(readOnly = true)
	public boolean existsByUsername(String username) {
		return memberRepository.existsByUsername(username);
	}
	
	/**
	 * 사용자 프로필 업데이트
	 * 닉네임과 프로필 이미지 URL을 수정합니다.
	 * 프로필 이미지는 ImageUploadController를 통해 미리 업로드된 URL을 사용합니다.
	 */
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
		// 새 이미지가 있다면 사용 확정
		if (newProfileImageUrl != null && !newProfileImageUrl.trim().isEmpty()) {
			fileManager.confirmUsingImage(newProfileImageUrl);
			log.info("새 프로필 이미지 사용 확정: {}", newProfileImageUrl);
		}

		// 기존 이미지가 있고 새 이미지와 다르다면 사용 중지
		if (oldProfileImageUrl != null && !oldProfileImageUrl.trim().isEmpty() 
			&& !oldProfileImageUrl.equals(newProfileImageUrl)) {
			fileManager.unUseImage(oldProfileImageUrl);
			log.info("기존 프로필 이미지 사용 중지: {}", oldProfileImageUrl);
		}
	}

}
