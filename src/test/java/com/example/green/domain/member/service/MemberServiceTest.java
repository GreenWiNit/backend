package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 닉네임 중복 확인 테스트")
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

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

} 