package com.example.green.domain.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.common.service.FileManager;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.dto.PhoneInfoResultDto;
import com.example.green.domain.pointshop.delivery.client.PhoneVerificationClient;
import com.example.green.global.error.exception.BusinessException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class MemberPhoneInfoTest {

	@Mock
	private MemberRepository memberRepository;
	
	@Mock
	private FileManager fileManager;
	
	@Mock
	private PhoneVerificationClient phoneVerificationClient;
	
	@InjectMocks
	private MemberService memberService;

	@Test
	void 휴대폰_번호가_등록된_회원의_인증된_휴대폰_정보를_조회한다() {
		// given
		Long memberId = 1L;
		String phoneNumber = "010-1234-5678";
		Member member = createMemberWithPhoneNumber(memberId, phoneNumber);
		
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(phoneVerificationClient.isAuthenticated(phoneNumber)).thenReturn(true);
		
		// when
		PhoneInfoResultDto result = memberService.getPhoneInfo(memberId);
		
		// then
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.isAuthenticated()).isTrue();
		verify(phoneVerificationClient).isAuthenticated(phoneNumber);
	}

	@Test
	void 휴대폰_번호가_등록된_회원의_인증되지_않은_휴대폰_정보를_조회한다() {
		// given
		Long memberId = 1L;
		String phoneNumber = "010-1234-5678";
		Member member = createMemberWithPhoneNumber(memberId, phoneNumber);
		
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(phoneVerificationClient.isAuthenticated(phoneNumber)).thenReturn(false);
		
		// when
		PhoneInfoResultDto result = memberService.getPhoneInfo(memberId);
		
		// then
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.isAuthenticated()).isFalse();
		verify(phoneVerificationClient).isAuthenticated(phoneNumber);
	}

	@Test
	void 휴대폰_번호가_등록되지_않은_회원의_휴대폰_정보를_조회한다() {
		// given
		Long memberId = 1L;
		Member member = createMemberWithoutPhoneNumber(memberId);
		
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		
		// when
		PhoneInfoResultDto result = memberService.getPhoneInfo(memberId);
		
		// then
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.isAuthenticated()).isFalse();
		verify(phoneVerificationClient, never()).isAuthenticated(any());
	}

	@Test
	void 휴대폰_인증_상태_확인_중_예외가_발생하면_인증되지_않은_상태로_처리한다() {
		// given
		Long memberId = 1L;
		String phoneNumber = "010-1234-5678";
		Member member = createMemberWithPhoneNumber(memberId, phoneNumber);
		
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
		when(phoneVerificationClient.isAuthenticated(phoneNumber))
			.thenThrow(new RuntimeException("인증 서비스 오류"));
		
		// when
		PhoneInfoResultDto result = memberService.getPhoneInfo(memberId);
		
		// then
		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.isAuthenticated()).isFalse();
	}

	@Test
	void 존재하지_않는_회원의_휴대폰_정보_조회_시_예외가_발생한다() {
		// given
		Long memberId = 999L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
		
		// when & then
		assertThatThrownBy(() -> memberService.getPhoneInfo(memberId))
			.isInstanceOf(BusinessException.class)
			.hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
	}

	private Member createMemberWithPhoneNumber(Long id, String phoneNumber) {
		Member member = Member.create("test provider123", "테스트사용자", "test@example.com");
		member.updatePhoneNumber(phoneNumber);
		return member;
	}

	private Member createMemberWithoutPhoneNumber(Long id) {
		Member member = Member.create("test provider123", "테스트사용자", "test@example.com");
		return member;
	}
} 