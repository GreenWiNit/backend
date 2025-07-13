package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.enums.MemberStatus;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private FileManager fileManager;

	@InjectMocks
	private MemberService memberService;

	private Member normalMember;
	private Member withdrawnMember;

	@BeforeEach
	void setUp() {
		normalMember = Member.create("google 123", "정상회원", "normal@example.com");
		normalMember.updateProfile("정상회원닉네임", "https://example.com/profile.jpg");
		setId(normalMember, 1L);

		withdrawnMember = Member.create("google 456", "탈퇴회원", "withdrawn@example.com");
		withdrawnMember.withdraw();
		setId(withdrawnMember, 2L);
	}

	private void setId(Member member, Long id) {
		try {
			var idField = Member.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(member, id);
		} catch (Exception e) {
			throw new RuntimeException("Failed to set ID for test", e);
		}
	}

	@Test
	@DisplayName("정상 회원을 ID로 탈퇴 처리할 수 있다")
	void withdrawMember_WithValidId_ShouldSucceed() {
		// given
		Long memberId = 1L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(normalMember));

		// when
		memberService.withdrawMember(memberId);

		// then
		assertThat(normalMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(normalMember.isDeleted()).isTrue();
		assertThat(normalMember.getLastLoginAt()).isNull();
		
		// 프로필 이미지 사용 중지 확인
		verify(fileManager).unUseImage("https://example.com/profile.jpg");
	}

	@Test
	@DisplayName("존재하지 않는 회원 ID로 탈퇴 시 예외 발생")
	void withdrawMember_WithNonExistentId_ShouldThrowException() {
		// given
		Long memberId = 999L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.withdrawMember(memberId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("이미 탈퇴한 회원을 다시 탈퇴 시 예외 발생")
	void withdrawMember_WithAlreadyWithdrawnMember_ShouldThrowException() {
		// given
		Long memberId = 2L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(withdrawnMember));

		// when & then
		assertThatThrownBy(() -> memberService.withdrawMember(memberId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN.getMessage());
	}

	@Test
	@DisplayName("정상 회원을 username으로 탈퇴 처리할 수 있다")
	void withdrawMemberByUsername_WithValidUsername_ShouldSucceed() {
		// given
		String username = "google 123";
		when(memberRepository.findByMemberKey(username)).thenReturn(Optional.of(normalMember));
		when(memberRepository.findById(1L)).thenReturn(Optional.of(normalMember));

		// when
		memberService.withdrawMemberByMemberKey(username);

		// then
		assertThat(normalMember.getStatus()).isEqualTo(MemberStatus.DELETED);
		assertThat(normalMember.isDeleted()).isTrue();
		
		// 프로필 이미지 사용 중지 확인
		verify(fileManager).unUseImage("https://example.com/profile.jpg");
	}

	@Test
	@DisplayName("존재하지 않는 username으로 탈퇴 시 예외 발생")
	void withdrawMemberByUsername_WithNonExistentUsername_ShouldThrowException() {
		// given
		String username = "nonexistent";
		when(memberRepository.findByMemberKey(username)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.withdrawMemberByMemberKey(username))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("프로필 이미지가 없는 회원 탈퇴 시 파일 관리자 호출하지 않음")
	void withdrawMember_WithoutProfileImage_ShouldNotCallFileManager() {
		// given
		Long memberId = 3L;
		Member memberWithoutImage = Member.create("google 789", "이미지없는회원", "noimage@example.com");
		setId(memberWithoutImage, memberId);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberWithoutImage));

		// when
		memberService.withdrawMember(memberId);

		// then
		verify(fileManager, never()).unUseImage(anyString());
	}

	@Test
	@DisplayName("활성 회원 조회가 정상적으로 동작한다")
	void findActiveByUsername_ShouldReturnActiveMember() {
		// given
		String username = "google 123";
		when(memberRepository.findActiveByMemberKey(username)).thenReturn(Optional.of(normalMember));

		// when
		Optional<Member> result = memberService.findActiveByMemberKey(username);

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getName()).isEqualTo("정상회원");
		verify(memberRepository).findActiveByMemberKey(username);
	}

	@Test
	@DisplayName("활성 회원 존재 여부 확인이 정상적으로 동작한다")
	void existsActiveByUsername_ShouldReturnCorrectResult() {
		// given
		String username = "google 123";
		when(memberRepository.existsActiveByMemberKey(username)).thenReturn(true);

		// when
		boolean result = memberService.existsActiveByMemberKey(username);

		// then
		assertThat(result).isTrue();
		verify(memberRepository).existsActiveByMemberKey(username);
	}

	@Test
	@DisplayName("회원 ID로 조회 시 존재하지 않는 경우 예외 발생")
	void findMemberById_WithNonExistentId_ShouldThrowException() {
		// given
		Long memberId = 999L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> memberService.withdrawMember(memberId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	@Test
	@DisplayName("프로필 업데이트가 정상적으로 동작한다")
	void updateProfile_ShouldUpdateProfileAndManageFiles() {
		// given
		Long memberId = 1L;
		String newNickname = "새로운닉네임";
		String newProfileImageUrl = "https://example.com/new-profile.jpg";
		String oldProfileImageUrl = "https://example.com/profile.jpg";
		
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(normalMember));

		// when
		memberService.updateProfile(memberId, newNickname, newProfileImageUrl);

		// then
		assertThat(normalMember.getProfile().getNickname()).isEqualTo(newNickname);
		assertThat(normalMember.getProfile().getProfileImageUrl()).isEqualTo(newProfileImageUrl);
		
		// 파일 관리 확인
		verify(fileManager).confirmUsingImage(newProfileImageUrl);
		verify(fileManager).unUseImage(oldProfileImageUrl);
	}
} 