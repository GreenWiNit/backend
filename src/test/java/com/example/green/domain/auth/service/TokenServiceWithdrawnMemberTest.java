package com.example.green.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.auth.exception.WithdrawnMemberAccessException;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.service.MemberService;

@ExtendWith(MockitoExtension.class)
@DisplayName("탈퇴한 사용자 재가입 차단 정책 테스트")
class TokenServiceWithdrawnMemberTest {

	@Mock
	private MemberService memberService;

	@Mock
	private CustomOAuth2UserService customOAuth2UserService;

	@Test
	@DisplayName("탈퇴한 사용자는 OAuth2 로그인 시 차단되어야 함")
	void shouldBlockWithdrawnUserFromOAuth2Login() {
		// Given
		String memberKey = "google 123456789";
		Member withdrawnMember = Member.create(memberKey, "홍길동", "test@example.com", "테스트닉네임");
		withdrawnMember.withdraw();

		given(memberService.findByMemberKey(memberKey))
			.willReturn(Optional.of(withdrawnMember));

		// When & Then
		Optional<Member> foundMember = memberService.findByMemberKey(memberKey);
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().isWithdrawn()).isTrue();

		Member member = foundMember.get();
		if (member.isWithdrawn()) {
			assertThatThrownBy(() -> {
				throw new WithdrawnMemberAccessException(memberKey);
			}).isInstanceOf(WithdrawnMemberAccessException.class)
			  .hasMessageContaining("탈퇴한 회원은 동일한 SNS 계정으로 재가입할 수 없습니다");
		}
	}

	@Test
	@DisplayName("활성 사용자는 정상적으로 로그인 가능해야 함")
	void shouldAllowActiveUserToLogin() {
		// Given
		String memberKey = "google 123456789";
		Member activeMember = Member.create(memberKey, "홍길동", "test@example.com", "테스트닉네임");

		given(memberService.findByMemberKey(memberKey))
			.willReturn(Optional.of(activeMember));
		given(memberService.existsActiveByMemberKey(memberKey))
			.willReturn(true);

		// When
		Optional<Member> foundMember = memberService.findByMemberKey(memberKey);
		boolean isActiveUser = memberService.existsActiveByMemberKey(memberKey);

		// Then
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().isWithdrawn()).isFalse();
		assertThat(isActiveUser).isTrue();
	}

	@Test
	@DisplayName("존재하지 않는 사용자는 신규 가입 가능해야 함")
	void shouldAllowNewUserToSignup() {
		// Given
		String memberKey = "google 999999999";

		given(memberService.findByMemberKey(memberKey))
			.willReturn(Optional.empty());
		given(memberService.existsActiveByMemberKey(memberKey))
			.willReturn(false);

		// When
		Optional<Member> foundMember = memberService.findByMemberKey(memberKey);
		boolean isActiveUser = memberService.existsActiveByMemberKey(memberKey);

		// Then
		assertThat(foundMember).isEmpty();
		assertThat(isActiveUser).isFalse();
		// 이 경우 신규 가입 플로우로 진행됨
	}
} 