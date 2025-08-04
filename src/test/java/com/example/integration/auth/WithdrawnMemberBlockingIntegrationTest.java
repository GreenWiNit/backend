package com.example.integration.auth;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.service.MemberService;
import com.example.integration.common.BaseIntegrationTest;

@DisplayName("탈퇴한 사용자 재가입 차단 정책 통합 테스트")
class WithdrawnMemberBlockingIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberRepository memberRepository;

	private Member withdrawnMember;
	private String memberKey;

	@BeforeEach
	void setUp() {
		// Given
		memberKey = "google 123456789";

		String originalMemberKey = memberService.signupFromOAuth2(
			"google", 
			"123456789", 
			"홍길동", 
			"test@example.com",
			"원래닉네임",
			"https://example.com/old-profile.jpg"
		);

		withdrawnMember = memberRepository.findByMemberKey(originalMemberKey).orElseThrow();

		withdrawnMember.withdraw();
		memberRepository.save(withdrawnMember);

		assertThat(withdrawnMember.isWithdrawn()).isTrue();
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.DELETED);
	}

	@Test
	@DisplayName("탈퇴한 사용자 재가입 시도 시 예외 발생해야 한다")
	@Transactional
	void shouldBlockWithdrawnUserReSignup() {
		// When & Then
		assertThatThrownBy(() -> 
			memberService.signupFromOAuth2(
				"google", "123456789", "홍길동", "test@example.com", "새닉네임", "new.jpg"
			)
		).isInstanceOf(IllegalStateException.class)
		 .hasMessageContaining("탈퇴한 사용자는 재가입할 수 없습니다");
	}


	@Test
	@DisplayName("다른 SNS 계정으로는 정상 가입 가능해야 한다")
	@Transactional
	void shouldAllowSignupWithDifferentSNSAccount() {
		// When: 다른 SNS 계정으로 가입
		String newMemberKey = memberService.signupFromOAuth2(
			"naver", "999999999",
			"김철수", 
			"new@example.com",
			"새사용자",
			"https://example.com/new-profile.jpg"
		);

		// Then
		assertThat(newMemberKey).isEqualTo("naver 999999999");
		
		Member newMember = memberRepository.findByMemberKey(newMemberKey).orElseThrow();
		assertThat(newMember.isWithdrawn()).isFalse();
		assertThat(newMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
	}
} 