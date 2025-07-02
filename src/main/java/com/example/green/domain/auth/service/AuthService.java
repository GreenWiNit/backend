package com.example.green.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

	private final MemberRepository memberRepository;

	// 회원가입 처리
	public String signup(TempTokenInfoDto tempTokenInfoDto, String nickname, String profileImageUrl) {
		// 임시 토큰에서 추출한 정보로 사용자 생성
		String provider = tempTokenInfoDto.getProvider();
		String providerId = tempTokenInfoDto.getProviderId();
		String username = provider + " " + providerId;

		// 이미 존재하는 사용자인지 확인
		Member existingMember = memberRepository.findByUsername(username);
		if (existingMember != null) {
			log.warn("이미 존재하는 사용자입니다: {}", username);
			return username;
		}

		// 새로운 멤버 생성
		Member member = Member.createOAuth2Member(
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

		log.info("신규 사용자 회원가입 완료: {} ({})", tempTokenInfoDto.getName(), tempTokenInfoDto.getEmail());
		return username;
	}
} 