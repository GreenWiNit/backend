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
import org.springframework.dao.DataIntegrityViolationException;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MemberService 탈퇴 사용자 복원 테스트")
class MemberServiceWithdrawnRestoreTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FileManager fileManager;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("탈퇴한 사용자가 재가입 시 새 정보로 복원되어야 한다")
	void shouldRestoreWithdrawnMemberWithNewInfo() {
		// Given
		String memberKey = "google 123456789";
		String name = "홍길동";
		String email = "test@example.com";
		String newNickname = "새로운닉네임";
		String newProfileImageUrl = "https://example.com/new-profile.jpg";

		Member withdrawnMember = Member.create(memberKey, "기존이름", "old@example.com");
		withdrawnMember.withdraw();

		given(memberRepository.findByMemberKey(memberKey))
			.willReturn(Optional.of(withdrawnMember));

		// When
		String result = memberService.signupFromOAuth2(
			"google", "123456789", name, email, newNickname, newProfileImageUrl
		);

		// Then
		assertThat(result).isEqualTo(memberKey);
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(withdrawnMember.isWithdrawn()).isFalse();
		assertThat(withdrawnMember.getName()).isEqualTo(name);
		assertThat(withdrawnMember.getEmail()).isEqualTo(email);
		assertThat(withdrawnMember.getProfile().getNickname()).isEqualTo(newNickname);
		assertThat(withdrawnMember.getProfile().getProfileImageUrl()).isEqualTo(newProfileImageUrl);
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
	@DisplayName("동시 회원가입 시 중복 예외 처리가 정상 작동해야 한다")
	void shouldHandleConcurrentSignupGracefully() {
		// Given
		String memberKey = "google 123456789";
		String name = "홍길동";
		String email = "test@example.com";

		given(memberRepository.findByMemberKey(memberKey))
			.willReturn(Optional.empty());
		given(memberRepository.save(any(Member.class)))
			.willThrow(new DataIntegrityViolationException("Duplicate key"));

		// When
		String result = memberService.signupFromOAuth2(
			"google", "123456789", name, email, "닉네임", "profile.jpg"
		);

		// Then
		assertThat(result).isEqualTo(memberKey);
	}

	@Test
	@DisplayName("탈퇴 사용자 복원 시 멱등성이 보장되어야 한다")
	void shouldEnsureIdempotencyOnRestore() {
		// Given
		String memberKey = "google 123456789";
		Member withdrawnMember = Member.create(memberKey, "홍길동", "test@example.com");
		withdrawnMember.withdraw();

		// When
		memberService.restoreStatusToNormal(withdrawnMember);
		memberService.restoreStatusToNormal(withdrawnMember);

		// Then
		assertThat(withdrawnMember.getStatus()).isEqualTo(MemberStatus.NORMAL);
		assertThat(withdrawnMember.isWithdrawn()).isFalse();
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
	@DisplayName("탈퇴한 사용자 재가입 시 OAuth2 정보와 사용자 입력이 모두 반영되어야 한다")
	void shouldUpdateBothOAuth2InfoAndUserInput() {
		// Given
		String memberKey = "google 123456789";
		Member withdrawnMember = Member.create(memberKey, "기존이름", "old@example.com");
		withdrawnMember.updateProfile("기존닉네임", "old-profile.jpg");
		withdrawnMember.withdraw();

		given(memberRepository.findByMemberKey(memberKey))
			.willReturn(Optional.of(withdrawnMember));

		// When
		memberService.signupFromOAuth2(
			"google", "123456789", 
			"홍길동 Updated",
			"new@example.com",
			"새로운닉네임",
			"new-profile.jpg"
		);

		// Then
		assertThat(withdrawnMember.getName()).isEqualTo("홍길동 Updated");
		assertThat(withdrawnMember.getEmail()).isEqualTo("new@example.com");
		assertThat(withdrawnMember.getProfile().getNickname()).isEqualTo("새로운닉네임");
		assertThat(withdrawnMember.getProfile().getProfileImageUrl()).isEqualTo("new-profile.jpg");
	}
} 