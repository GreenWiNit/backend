package com.example.green.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public String signup(TokenService.TempTokenInfo tempTokenInfo, String nickname, String profileImageUrl) {
		// 임시 토큰에서 추출한 정보로 사용자 생성
		String provider = "google"; // TODO: 임시 토큰에서 provider 정보도 추출하도록 개선
		String providerId = extractProviderIdFromEmail(tempTokenInfo.getEmail()); // 임시 방편
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
			tempTokenInfo.getName(),
			tempTokenInfo.getEmail()
		);
		
		// 사용자가 입력한 닉네임으로 업데이트 (선택사항)
		if (nickname != null && !nickname.trim().isEmpty()) {
			// TODO: Member 엔티티에 nickname 업데이트 메서드 추가 또는 기존 메서드 사용
			log.info("사용자 지정 닉네임: {}", nickname);
		}
		
		memberRepository.save(member);
		
		log.info("신규 사용자 회원가입 완료: {} ({})", tempTokenInfo.getName(), tempTokenInfo.getEmail());
		return username;
	}
	
	// 이메일에서 provider ID 추출하는 임시 방편 (실제로는 임시 토큰에서 가져와야 함)
	private String extractProviderIdFromEmail(String email) {
		// 임시 방편: 이메일을 해시해서 고유 ID 생성
		return String.valueOf(Math.abs(email.hashCode()));
	}
} 