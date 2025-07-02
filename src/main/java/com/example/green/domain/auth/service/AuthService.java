package com.example.green.domain.auth.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberRepository memberRepository;

	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 50, multiplier = 2.0, random = true)
	)
	public String signup(TempTokenInfoDto tempTokenInfoDto, String nickname, String profileImageUrl) {
		String provider = tempTokenInfoDto.getProvider();
		String providerId = tempTokenInfoDto.getProviderId();
		String username = provider + " " + providerId;

		// 낙관적 락: 일반 조회 후 저장 시 @Version 체크
		Member existingMember = memberRepository.findByUsername(username);
		if (existingMember != null) {
			log.warn("이미 존재하는 사용자입니다 (낙관적 락 체크): {}", username);
			return username;
		}

		try {
			// 새로운 멤버 생성
			Member member = Member.create(
				username,
				tempTokenInfoDto.getName(),
				tempTokenInfoDto.getEmail()
			);

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

			log.info("신규 사용자 회원가입 완료 (낙관적 락 + 재시도): {} ({})",
				tempTokenInfoDto.getName(), tempTokenInfoDto.getEmail());
			return username;

		} catch (DataIntegrityViolationException e) {
			//  DB 제약조건 위반 시 (동시 가입으로 인한 중복)
			log.warn("동시 회원가입으로 인한 중복 감지, 기존 사용자로 처리: {}", username, e);
			return username;
		}
	}

}