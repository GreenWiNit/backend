package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.service.FileService;
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
	private FileService fileService;

	@Mock
	private Member member;

	@Mock
	private Profile profile;
	
	@BeforeEach
	void setUp() {
		// 직접 생성자로 MemberService 생성
		memberService = new MemberService(memberRepository, fileManager, fileService);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임만 변경")
	void updateProfile_OnlyNickname_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(memberRepository.save(member)).willReturn(member);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, null);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, null);
		verify(memberRepository).save(member);
		verifyNoInteractions(fileService, fileManager);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임과 프로필 이미지 모두 변경")
	void updateProfile_NicknameAndImage_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String oldImageUrl = "old-image-url";
		String newImageUrl = "new-image-url";
		
		MultipartFile profileImage = new MockMultipartFile(
			"profileImage", 
			"test.jpg", 
			"image/jpeg", 
			"test image content".getBytes()
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(oldImageUrl);
		given(fileService.uploadImage(profileImage, Purpose.PROFILE)).willReturn(newImageUrl);
		given(memberRepository.save(member)).willReturn(member);
		
		// when
		Member result = memberService.updateProfile(memberId, newNickname, profileImage);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, newImageUrl);
		verify(fileService).uploadImage(profileImage, Purpose.PROFILE);
		verify(fileManager).confirmUsingImage(newImageUrl);
		verify(fileManager).unUseImage(oldImageUrl);
		verify(memberRepository).save(member);
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 기존 프로필 이미지가 없는 경우")
	void updateProfile_NoOldImage_Success() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String newImageUrl = "new-image-url";
		
		MultipartFile profileImage = new MockMultipartFile(
			"profileImage", 
			"test.jpg", 
			"image/jpeg", 
			"test image content".getBytes()
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn(null); // 기존 이미지 없음
		given(fileService.uploadImage(profileImage, Purpose.PROFILE)).willReturn(newImageUrl);
		given(memberRepository.save(member)).willReturn(member);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, profileImage);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, newImageUrl);
		verify(fileService).uploadImage(profileImage, Purpose.PROFILE);
		verify(fileManager).confirmUsingImage(newImageUrl);
		verify(fileManager, never()).unUseImage(any()); // 기존 이미지가 없으므로 호출되지 않음
		verify(memberRepository).save(member);
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

		verify(memberRepository, never()).save(any());
		verifyNoInteractions(fileService, fileManager);
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 파일 업로드 실패")
	void updateProfile_FileUploadFailed_ThrowsException() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		
		MultipartFile profileImage = new MockMultipartFile(
			"profileImage", 
			"test.jpg", 
			"image/jpeg", 
			"test image content".getBytes()
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(member.getProfile()).willReturn(profile);
		given(profile.getProfileImageUrl()).willReturn("old-image.jpg");
		given(fileService.uploadImage(profileImage, Purpose.PROFILE))
			.willThrow(new RuntimeException("파일 업로드 실패"));

		// when & then
		assertThatThrownBy(() -> memberService.updateProfile(memberId, newNickname, profileImage))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_PROFILE_UPDATE_FAILED.getMessage());

		verify(member, never()).updateProfile(any(), any()); // 프로필 업데이트되지 않음
		verify(memberRepository, never()).save(any()); // 저장하지 않음
		verifyNoInteractions(fileManager); // 파일 관리자 호출되지 않음
	}

	@Test
	@DisplayName("프로필 업데이트 - 빈 파일은 무시됨")
	void updateProfile_EmptyFile_Ignored() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		
		MultipartFile emptyFile = new MockMultipartFile(
			"profileImage", 
			"", 
			"image/jpeg", 
			new byte[0] // 빈 파일
		);

		given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
		given(memberRepository.save(member)).willReturn(member);

		// when
		Member result = memberService.updateProfile(memberId, newNickname, emptyFile);

		// then
		assertThat(result).isEqualTo(member);
		verify(member).updateProfile(newNickname, null); // 닉네임만 업데이트, 이미지는 null
		verifyNoInteractions(fileService, fileManager);
		verify(memberRepository).save(member);
	}
} 