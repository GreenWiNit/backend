package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("탈퇴한 사용자 재가입 차단 정책 테스트")
class MemberServiceWithdrawnRestoreTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FileManager fileManager;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("탈퇴한 사용자 재가입 시도 시 예외 발생해야 함")
	void shouldThrowExceptionWhenWithdrawnUserTriesToSignup() {
		// Given
		String memberKey = "google 123456789";
		Member withdrawnMember = Member.create(memberKey, "기존이름", "old@example.com");
		withdrawnMember.withdraw();

		given(memberRepository.findByMemberKey(memberKey))
			.willReturn(Optional.of(withdrawnMember));

		// When & Then
		assertThatThrownBy(() -> 
			memberService.signupFromOAuth2(
				"google", "123456789", "홍길동", "test@example.com", "새닉네임", "new.jpg"
			)
		).isInstanceOf(IllegalStateException.class)
		 .hasMessageContaining("탈퇴한 사용자는 재가입할 수 없습니다");
	}

	@Test
	@DisplayName("존재하지 않는 사용자는 새로 생성해야 한다")
	void shouldCreateNewMemberWhenNotExists() {
		// Given
		String memberKey = "google 123456789";
		String name = "홍길동";
		String email = "test@example.com";
		String nickname = "닉네임";
		String profileImageUrl = "https://example.com/profile.jpg";

		given(memberRepository.findByMemberKey(memberKey))
			.willReturn(Optional.empty());

		// When
		String result = memberService.signupFromOAuth2(
			"google", "123456789", name, email, nickname, profileImageUrl
		);

		// Then
		assertThat(result).isEqualTo(memberKey);
		verify(memberRepository).save(any(Member.class));
	}

	@Test
	@DisplayName("활성 사용자 존재 여부 확인이 정확해야 한다")
	void shouldCheckActiveUserExistenceCorrectly() {
		// Given
		String activeUserKey = "google 111111111";
		String withdrawnUserKey = "google 222222222";

		given(memberRepository.existsActiveByMemberKey(activeUserKey))
			.willReturn(true);
		given(memberRepository.existsActiveByMemberKey(withdrawnUserKey))
			.willReturn(false);

		// When & Then
		assertThat(memberService.existsActiveByMemberKey(activeUserKey)).isTrue();
		assertThat(memberService.existsActiveByMemberKey(withdrawnUserKey)).isFalse();
	}

	@Test
	@DisplayName("탈퇴한 사용자와 활성 사용자 구분이 정확해야 함")
	void shouldDistinguishBetweenWithdrawnAndActiveUsers() {
		// Given
		String memberKey = "google 123456789";
		
		// 탈퇴한 사용자
		Member withdrawnMember = Member.create(memberKey, "홍길동", "test@example.com");
		withdrawnMember.withdraw();
		
		// 활성 사용자  
		Member activeMember = Member.create("google 987654321", "김철수", "active@example.com");

		// When & Then
		assertThat(withdrawnMember.isWithdrawn()).isTrue();
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		
		assertThat(activeMember.isWithdrawn()).isFalse();
		assertThat(activeMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
	}
} 