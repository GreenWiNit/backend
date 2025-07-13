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

	// 재시도 설정 상수들
	private static final int SIGNUP_MAX_ATTEMPTS = 3;
	private static final int SIGNUP_DELAY = 50;
	private static final double SIGNUP_MULTIPLIER = 2.0;
	
	private static final int GENERAL_MAX_ATTEMPTS = 3;
	private static final int GENERAL_DELAY = 100;
	private static final double GENERAL_MULTIPLIER = 2.0;

	/**
	 * OAuth2 회원가입 처리
	 *
	 * OAuth2 인증 정보를 기반으로 새로운 회원을 생성.
	 * provider와 providerId를 조합하여 고유한 memberKey를 생성하고,
	 * 사용자가 입력한 추가 정보(닉네임, 프로필 이미지)와 함께 회원을 등록.
	 *
	 * @param provider OAuth2 제공자 (google, naver, kakao 등)
	 * @param providerId OAuth2 제공자에서 발급한 고유 ID
	 * @param name OAuth2에서 제공하는 사용자 이름
	 * @param email OAuth2에서 제공하는 이메일 주소
	 * @param nickname 사용자가 입력한 닉네임
	 * @param profileImageUrl 사용자가 선택한 프로필 이미지 URL
	 * @return 생성된 사용자의 memberKey (provider + " " + providerId)
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = SIGNUP_MAX_ATTEMPTS,
		backoff = @Backoff(delay = SIGNUP_DELAY, multiplier = SIGNUP_MULTIPLIER, random = true)
	)
	public String signupFromOAuth2(String provider, String providerId, String name, String email,
		String nickname, String profileImageUrl) {

		String memberKey = createMemberKey(provider, providerId);

		return createMember(memberKey, name, email, nickname, profileImageUrl);
	}

	/**
	 * OAuth2 정보로 memberKey 생성
	 *
	 * OAuth2 provider와 providerId를 조합하여 고유한 memberKey를 생성.
	 * 형식: "provider providerId" (예: "google 123456789")
	 *
	 * @param provider OAuth2 제공자
	 * @param providerId OAuth2 제공자에서 발급한 고유 ID
	 * @return 생성된 memberKey
	 */
	private String createMemberKey(String provider, String providerId) {
		return provider + " " + providerId;
	}

	/**
	 * 회원 생성 및 저장
	 * 주어진 정보로 새로운 회원을 생성하고 데이터베이스에 저장.
	 * 동시성 문제로 인한 중복 생성을 방지하기 위해 예외 처리를 포함.
	 *
	 * @param memberKey 고유 사용자명
	 * @param name 사용자 이름
	 * @param email 이메일 주소
	 * @param nickname 닉네임 (선택사항)
	 * @param profileImageUrl 프로필 이미지 URL (선택사항)
	 * @return 생성된 사용자의 memberKey
	 */
	private String createMember(String memberKey, String name, String email, String nickname, String profileImageUrl) {
		if (memberRepository.existsByMemberKey(memberKey)) {
			log.warn("이미 존재하는 사용자입니다 (DB 체크): {}", memberKey);
			return memberKey;
		}

		try {
			Member member = Member.create(memberKey, name, email);

			// 추가 프로필 정보가 있는 경우 업데이트
			if (hasAdditionalProfileInfo(nickname, profileImageUrl)) {
				member.updateProfile(nickname, profileImageUrl);
				log.info("사용자 지정 프로필 설정: nickname={}, profileImageUrl={}", nickname, profileImageUrl);
			}

			memberRepository.save(member);
			log.info("신규 사용자 회원가입 완료: {} ({})", name, email);
			return memberKey;

		} catch (DataIntegrityViolationException e) {
			log.warn("동시 회원가입으로 인한 중복 감지, 기존 사용자로 처리: {}", memberKey, e);
			return memberKey;
		}
	}

	/**
	 * 추가 프로필 정보가 있는지 확인
	 */
	private boolean hasAdditionalProfileInfo(String nickname, String profileImageUrl) {
		return (nickname != null && !nickname.trim().isEmpty()) ||
			(profileImageUrl != null && !profileImageUrl.trim().isEmpty());
	}

	/**
	 * OAuth2 정보 업데이트 (기존 사용자)
	 *
	 * 기존 회원의 OAuth2 정보를 최신 정보로 업데이트.
	 * 로그인 시점에 OAuth2 제공자로부터 받은 최신 정보로 갱신하여
	 * 사용자 정보의 일관성을 유지.
	 *
	 * @param memberKey 업데이트할 사용자의 memberKey
	 * @param name OAuth2에서 제공하는 최신 사용자 이름
	 * @param email OAuth2에서 제공하는 최신 이메일 주소
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = GENERAL_MAX_ATTEMPTS,
		backoff = @Backoff(delay = GENERAL_DELAY, multiplier = GENERAL_MULTIPLIER, random = true)
	)
	public void updateOAuth2Info(String memberKey, String name, String email) {
		memberRepository.findByMemberKey(memberKey)
			.ifPresent(member -> {
				member.updateOAuth2Info(name, email);
				log.debug("OAuth2 사용자 정보 업데이트 완료: {}", memberKey);
			});
	}

	/**
	 * 활성 회원만 조회 (탈퇴하지 않은 회원)
	 * 토큰 검증 등에서 사용되며, 탈퇴한 회원의 토큰을 자동으로 무효화
	 */
	@Transactional(readOnly = true)
	public Optional<Member> findActiveByMemberKey(String memberKey) {
		return memberRepository.findActiveByMemberKey(memberKey);
	}

	/**
	 * 활성 회원 존재 여부 확인
	 */
	@Transactional(readOnly = true)
	public boolean existsActiveByMemberKey(String memberKey) {
		return memberRepository.existsActiveByMemberKey(memberKey);
	}

	/**
	 * 회원 프로필 업데이트
	 * - 닉네임과 프로필 이미지 URL 업데이트
	 * - 프로필 이미지 파일 관리 처리
	 */
	@Transactional
	public Member updateProfile(Long memberId, String nickname, String profileImageUrl) {
		Member member = findMemberById(memberId, "프로필 업데이트");

		String oldProfileImageUrl = member.getProfile().getProfileImageUrl();

		member.updateProfile(nickname, profileImageUrl);

		processProfileImageUpdate(oldProfileImageUrl, profileImageUrl);

		log.info("프로필 업데이트 완료: memberId={}, nickname={}, profileImageUrl={}",
			memberId, nickname, profileImageUrl);

		return member;
	}

	private Member findMemberById(Long memberId, String operation) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> {
				log.error("{} 실패: 사용자를 찾을 수 없음 - memberId: {}", operation, memberId);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
	}


	private void processProfileImageUpdate(String oldProfileImageUrl, String newProfileImageUrl) {

		confirmNewProfileImage(newProfileImageUrl);

		if (isProfileImageChanged(oldProfileImageUrl, newProfileImageUrl)) {
			unUseProfileImage(oldProfileImageUrl);
		}
	}


	private void confirmNewProfileImage(String profileImageUrl) {
		if (StringUtils.hasText(profileImageUrl)) {
			fileManager.confirmUsingImage(profileImageUrl);
			log.info("새 프로필 이미지 사용 확정: {}", profileImageUrl);
		}
	}


	private void unUseProfileImage(String profileImageUrl) {
		if (StringUtils.hasText(profileImageUrl)) {
			fileManager.unUseImage(profileImageUrl);
			log.info("프로필 이미지 사용 중지: {}", profileImageUrl);
		}
	}

	private boolean isProfileImageChanged(String oldProfileImageUrl, String newProfileImageUrl) {
		return StringUtils.hasText(oldProfileImageUrl)
			&& !oldProfileImageUrl.equals(newProfileImageUrl);
	}

	/**
	 * 회원 탈퇴 처리 (본인 탈퇴)
	 * 회원 상태를 DELETED로 변경하고, 관련 파일 리소스를 정리.
	 *
	 * @param memberId 탈퇴할 회원 ID
	 * @throws BusinessException 회원을 찾을 수 없거나 이미 탈퇴한 경우
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = GENERAL_MAX_ATTEMPTS,
		backoff = @Backoff(delay = GENERAL_DELAY, multiplier = GENERAL_MULTIPLIER, random = true)
	)
	public void withdrawMember(Long memberId) {
		Member member = findMemberById(memberId, "회원 탈퇴");

		validateMemberCanWithdraw(member);
		processProfileImageOnWithdraw(member);

		member.withdraw();

		log.info("회원 탈퇴 완료 (본인 탈퇴): memberId={}, memberKey={}",
			memberId, member.getMemberKey());

		// TODO: 배치 시스템으로 물리적 삭제 처리
		// - 탈퇴 후 일정 기간(예: 30일) 경과 시 개인정보 완전 삭제
		// - 관련 토큰, 포인트 내역, 주문 내역 등도 함께 처리
		// - 법적 보관 의무가 있는 데이터는 별도 보관소로 이관
	}

	/**
	 * 관리자에 의한 회원 강제 삭제 처리
	 * 관리자가 회원을 강퇴하거나 삭제 시 사용.
	 * 
	 * @param memberId 삭제할 회원 ID
	 * @throws BusinessException 회원을 찾을 수 없거나 이미 탈퇴한 경우
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = GENERAL_MAX_ATTEMPTS,
		backoff = @Backoff(delay = GENERAL_DELAY, multiplier = GENERAL_MULTIPLIER, random = true)
	)
	public void forceDeleteMemberByAdmin(Long memberId) {
		Member member = findMemberById(memberId, "관리자 강제 삭제");

		validateMemberCanWithdraw(member);
		processProfileImageOnWithdraw(member);

		member.withdraw();

		log.warn("관리자에 의한 강제 삭제 완료: memberId={}, memberKey={}",
			memberId, member.getMemberKey());

		// TODO: 관리자 삭제 감사 로그 별도 기록
		// - 삭제한 관리자 정보
		// - 삭제 시점
		// - 복구 가능 기간 설정
	}

	/**
	 * 탈퇴 가능한 회원인지 검증
	 */
	private void validateMemberCanWithdraw(Member member) {
		if (member.isWithdrawn()) {
			log.warn("이미 탈퇴한 회원입니다: memberId={}", member.getId());
			throw new BusinessException(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN);
		}
	}

	/**
	 * 탈퇴 시 프로필 이미지 처리
	 */
	private void processProfileImageOnWithdraw(Member member) {
		if (member.getProfile().hasProfileImage()) {
			unUseProfileImage(member.getProfile().getProfileImageUrl());
			log.info("회원 탈퇴로 인한 프로필 이미지 사용 중지: {}", member.getProfile().getProfileImageUrl());
		}
	}

	/**
	 * memberKey로 회원 탈퇴 처리
	 * Auth 도메인에서 토큰 무효화 후 Member 도메인 탈퇴 처리를 위해 호출됩니다.
	 *
	 * @param memberKey 탈퇴할 사용자의 memberKey
	 * @throws BusinessException 회원을 찾을 수 없는 경우
	 */
	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = GENERAL_MAX_ATTEMPTS,
		backoff = @Backoff(delay = GENERAL_DELAY, multiplier = GENERAL_MULTIPLIER, random = true)
	)
	public void withdrawMemberByMemberKey(String memberKey) {
		Member member = findMemberByMemberKey(memberKey, "회원 탈퇴");
		withdrawMember(member.getId());
	}

	/**
	 * 사용자명으로 회원 조회
	 * - 회원이 존재하지 않으면 예외 발생
	 *
	 * @param memberKey 사용자명
	 * @param operation 수행 중인 작업
	 * @return 조회된 회원
	 * @throws BusinessException 회원이 존재하지 않는 경우
	 */
	private Member findMemberByMemberKey(String memberKey, String operation) {
		return memberRepository.findByMemberKey(memberKey)
			.orElseThrow(() -> {
				log.error("{} 실패: 사용자를 찾을 수 없음 - memberKey: {}", operation, memberKey);
				return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
			});
	}

}