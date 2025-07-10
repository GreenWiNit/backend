package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	private MemberService memberService;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FileManager fileManager;

	@Mock
	private Member member;

	@Mock
	private Profile profile;
	
	@BeforeEach
	void setUp() {
		// 직접 생성자로 MemberService 생성
		memberService = new MemberService(memberRepository, fileManager);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임만 변경")
	void updateProfile_OnlyNickname_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(null);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, null);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, null);
		verifyNoInteractions(fileManager);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임과 프로필 이미지 모두 변경")
	void updateProfile_NicknameAndImage_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String oldImageUrl = "old-image-url";
		String newImageUrl = "new-image-url";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(oldImageUrl);
		
		// when
		Member result = memberService.updateProfile(memberId, newNickname, newImageUrl);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, newImageUrl);
		verify(fileManager).confirmUsingImage(newImageUrl);
		verify(fileManager).unUseImage(oldImageUrl);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 기존 프로필 이미지가 없는 경우")
	void updateProfile_NoOldImage_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String newImageUrl = "new-image-url";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(null); // 기존 이미지 없음

		// when
		Member result = memberService.updateProfile(memberId, newNickname, newImageUrl);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, newImageUrl);
		verify(fileManager).confirmUsingImage(newImageUrl);
		verify(fileManager, never()).unUseImage(any()); // 기존 이미지가 없으므로 호출되지 않음
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 같은 이미지 URL로 변경하는 경우")
	void updateProfile_SameImageUrl_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String sameImageUrl = "same-image-url";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(sameImageUrl);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, sameImageUrl);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, sameImageUrl);
		verify(fileManager).confirmUsingImage(sameImageUrl);
		verify(fileManager, never()).unUseImage(any()); // 같은 이미지이므로 삭제하지 않음
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 존재하지 않는 사용자")
	void updateProfile_MemberNotFound_ThrowsException() {
		// given
		Long memberId = 999L;
		String newNickname = "새로운닉네임";

		given(memberRepository.findById(memberId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.updateProfile(memberId, newNickname, null))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());

		verifyNoInteractions(fileManager);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 빈 문자열 이미지 URL은 무시됨")
	void updateProfile_EmptyImageUrl_Ignored() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String emptyImageUrl = "";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(null);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, emptyImageUrl);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, emptyImageUrl);
		verifyNoInteractions(fileManager); // 빈 문자열이므로 파일 관리자 호출되지 않음
	}
} 