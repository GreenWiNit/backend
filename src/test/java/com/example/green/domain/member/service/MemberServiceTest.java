package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.infra.client.FileClient;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FileClient fileClient;

	@InjectMocks
	private MemberService memberService;

	@Test
	@DisplayName("사용 가능한 닉네임이면 true를 반환한다")
	void isNicknameAvailable_AvailableNickname_ReturnsTrue() {
		// Given
		String nickname = "validNick123";
		given(memberRepository.countByNickname(nickname)).willReturn(0L);

		// When
		boolean isAvailable = memberService.isNicknameAvailable(nickname);

		// Then
		assertThat(isAvailable).isTrue();
		verify(memberRepository).countByNickname(nickname);
	}

	@Test
	@DisplayName("이미 사용 중인 닉네임이면 false를 반환한다")
	void isNicknameAvailable_TakenNickname_ReturnsFalse() {
		// Given
		String nickname = "takenNickname";
		given(memberRepository.countByNickname(nickname)).willReturn(1L);

		// When
		boolean isAvailable = memberService.isNicknameAvailable(nickname);

		// Then
		assertThat(isAvailable).isFalse();
		verify(memberRepository).countByNickname(nickname);
	}

	@Test
	@DisplayName("한글, 영문, 숫자만 포함된 닉네임을 정상적으로 처리한다")
	void isNicknameAvailable_ValidCharacters_HandlesCorrectly() {
		// Given
		String validNickname = "한글English123";
		given(memberRepository.countByNickname(validNickname)).willReturn(0L);

		// When
		boolean isAvailable = memberService.isNicknameAvailable(validNickname);

		// Then
		assertThat(isAvailable).isTrue();
		verify(memberRepository).countByNickname(validNickname);
	}

	@Test
	@DisplayName("대소문자를 구분하여 처리한다")
	void isNicknameAvailable_CaseSensitive_HandlesCorrectly() {
		// Given
		String uppercaseNickname = "TestUser";
		String lowercaseNickname = "testuser";
		given(memberRepository.countByNickname(uppercaseNickname)).willReturn(0L);
		given(memberRepository.countByNickname(lowercaseNickname)).willReturn(1L);

		// When & Then
		assertThat(memberService.isNicknameAvailable(uppercaseNickname)).isTrue();
		assertThat(memberService.isNicknameAvailable(lowercaseNickname)).isFalse();

		verify(memberRepository).countByNickname(uppercaseNickname);
		verify(memberRepository).countByNickname(lowercaseNickname);
	}

	@Test
	@DisplayName("프로필 업데이트 - 닉네임만 변경")
	void updateProfile_OnlyNickname_Success() {
		// Given
		Long memberId = 1L;
		Member member = Member.create("test 123", "테스트", "test@test.com");
		member.updateProfile("기존닉네임", "https://s3.example.com/old-image.jpg");

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// When - null을 전달하면 기존 이미지가 유지됨
		Member updatedMember = memberService.updateProfile(memberId, "새닉네임", null);

		// Then
		assertThat(updatedMember.getProfile().getNickname()).isEqualTo("새닉네임");
		assertThat(updatedMember.getProfile().getProfileImageUrl()).isEqualTo("https://s3.example.com/old-image.jpg");
		// null로 전달한 경우 서비스 로직이 기존 이미지를 삭제 처리함
		verify(fileClient, never()).confirmUsingImage(any());
		verify(fileClient).unUseImage("https://s3.example.com/old-image.jpg");
	}

	@Test
	@DisplayName("프로필 업데이트 - 프로필 이미지만 변경")
	void updateProfile_OnlyProfileImage_Success() {
		// Given
		Long memberId = 1L;
		Member member = Member.create("test 123", "테스트", "test@test.com");
		member.updateProfile("기존닉네임", "https://s3.example.com/old-image.jpg");

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// When
		Member updatedMember = memberService.updateProfile(memberId, null, "https://s3.example.com/new-image.jpg");

		// Then
		assertThat(updatedMember.getProfile().getNickname()).isEqualTo("기존닉네임");
		assertThat(updatedMember.getProfile().getProfileImageUrl()).isEqualTo("https://s3.example.com/new-image.jpg");
		verify(fileClient).confirmUsingImage("https://s3.example.com/new-image.jpg");
		verify(fileClient).unUseImage("https://s3.example.com/old-image.jpg");
	}

	@Test
	@DisplayName("프로필 업데이트 - 닉네임과 프로필 이미지 모두 변경")
	void updateProfile_BothFields_Success() {
		// Given
		Long memberId = 1L;
		Member member = Member.create("test 123", "테스트", "test@test.com");
		member.updateProfile("기존닉네임", "https://s3.example.com/old-image.jpg");

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// When
		Member updatedMember = memberService.updateProfile(memberId, "새닉네임", "https://s3.example.com/new-image.jpg");

		// Then
		assertThat(updatedMember.getProfile().getNickname()).isEqualTo("새닉네임");
		assertThat(updatedMember.getProfile().getProfileImageUrl()).isEqualTo("https://s3.example.com/new-image.jpg");
		verify(fileClient).confirmUsingImage("https://s3.example.com/new-image.jpg");
		verify(fileClient).unUseImage("https://s3.example.com/old-image.jpg");
	}

	@Test
	@DisplayName("프로필 업데이트 - 프로필 이미지를 동일한 값으로 설정")
	void updateProfile_SameProfileImage_NoFileOperation() {
		// Given
		Long memberId = 1L;
		Member member = Member.create("test 123", "테스트", "test@test.com");
		member.updateProfile("기존닉네임", "https://s3.example.com/old-image.jpg");

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

		// When - 동일한 이미지 URL로 업데이트
		Member updatedMember = memberService.updateProfile(memberId, "새닉네임", "https://s3.example.com/old-image.jpg");

		// Then - 이미지가 동일하므로 파일 작업이 없음
		assertThat(updatedMember.getProfile().getNickname()).isEqualTo("새닉네임");
		assertThat(updatedMember.getProfile().getProfileImageUrl()).isEqualTo("https://s3.example.com/old-image.jpg");
		verify(fileClient).confirmUsingImage("https://s3.example.com/old-image.jpg");
		verify(fileClient, never()).unUseImage(any());
	}

} 