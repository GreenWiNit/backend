package com.example.green.domain.member.service;

import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.service.FileService;
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
	private final FileService fileService;

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
	
	public Member updateProfile(Long memberId, String nickname, MultipartFile profileImage) {
		Member member = findMemberById(memberId);
		String newProfileImageUrl = processProfileImageUpload(profileImage, member);
		updateMemberProfile(member, nickname, newProfileImageUrl, memberId);
		
		return memberRepository.save(member);
	}

	private Member findMemberById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.error("프로필 업데이트 실패: 사용자를 찾을 수 없음 - memberId: {}", memberId);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
	}

	private String processProfileImageUpload(MultipartFile profileImage, Member member) {
		if (profileImage == null || profileImage.isEmpty()) {
			return null;
		}

		String oldProfileImageUrl = member.getProfile().getProfileImageUrl();
		
		try {
			String newProfileImageUrl = fileService.uploadImage(profileImage, Purpose.PROFILE);

			confirmNewImageAndCleanupOld(newProfileImageUrl, oldProfileImageUrl);
			
			log.info("프로필 이미지 처리 완료: {} -> {}", oldProfileImageUrl, newProfileImageUrl);
			return newProfileImageUrl;
			
		} catch (Exception e) {
			log.error("프로필 이미지 업로드 실패: {}", e.getMessage());
			throw new BusinessException(MemberExceptionMessage.MEMBER_PROFILE_UPDATE_FAILED);
		}
	}

	private void confirmNewImageAndCleanupOld(String newProfileImageUrl, String oldProfileImageUrl) {

		fileManager.confirmUsingImage(newProfileImageUrl);
		log.info("새 프로필 이미지 사용 확정: {}", newProfileImageUrl);

		if (oldProfileImageUrl != null && !oldProfileImageUrl.trim().isEmpty()) {
			fileManager.unUseImage(oldProfileImageUrl);
			log.info("기존 프로필 이미지 사용 중지: {}", oldProfileImageUrl);
		}
	}

	private void updateMemberProfile(Member member, String nickname, String newProfileImageUrl, Long memberId) {
		member.updateProfile(nickname, newProfileImageUrl);
		log.info("프로필 업데이트 완료: memberId={}, nickname={}, profileImageUrl={}", 
			memberId, nickname, newProfileImageUrl);
	}

}
