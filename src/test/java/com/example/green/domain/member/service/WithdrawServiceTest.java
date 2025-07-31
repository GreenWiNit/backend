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

import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.member.dto.WithdrawRequestDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.WithdrawReason;
import com.example.green.domain.member.entity.enums.WithdrawReasonType;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.WithdrawReasonRepository;
import com.example.green.global.error.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private WithdrawReasonRepository withdrawReasonRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private WithdrawService withdrawService;

    @Test
    @DisplayName("정상적인 탈퇴 사유와 함께 회원 탈퇴 처리 성공")
    void withdrawMemberWithReason_Success() {
        // given
        String memberKey = "google 123456";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            "서비스가 불편해서 탈퇴합니다."
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(withdrawReasonRepository).save(any(WithdrawReason.class));
        verify(memberService).withdrawMember(member.getId());
        verify(authService).invalidateAllTokens(memberKey);
    }

    @Test
    @DisplayName("기타 사유 선택 시 상세 사유 입력 없으면 예외 발생")
    void withdrawMemberWithReason_OtherReasonWithoutCustomReason_ThrowsException() {
        // given
        String memberKey = "google 123456";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.OTHER,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberExceptionMessage.WITHDRAW_CUSTOM_REASON_REQUIRED.getMessage());
            
        verify(withdrawReasonRepository, never()).save(any());
        verify(memberService, never()).withdrawMember(any());
        verify(authService, never()).invalidateAllTokens(any());
    }

    @Test
    @DisplayName("기타 사유 선택 시 빈 문자열이면 예외 발생")
    void withdrawMemberWithReason_OtherReasonWithEmptyCustomReason_ThrowsException() {
        // given
        String memberKey = "google 123456";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.OTHER,
            "   "
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberExceptionMessage.WITHDRAW_CUSTOM_REASON_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 회원 탈퇴 시도 시 예외 발생")
    void withdrawMemberWithReason_MemberNotFound_ThrowsException() {
        // given
        String memberKey = "nonexistent";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            "불편해서 탈퇴"
        );
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberExceptionMessage.MEMBER_NOT_FOUND.getMessage());
            
        verify(withdrawReasonRepository, never()).save(any());
        verify(memberService, never()).withdrawMember(any());
        verify(authService, never()).invalidateAllTokens(any());
    }

    @Test
    @DisplayName("이미 탈퇴한 회원 탈퇴 시도 시 예외 발생")
    void withdrawMemberWithReason_AlreadyWithdrawnMember_ThrowsException() {
        // given
        String memberKey = "google 123456";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            "불편해서 탈퇴"
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com");
        member.withdraw(); // 이미 탈퇴한 상태로 설정
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN.getMessage());
            
        verify(withdrawReasonRepository, never()).save(any());
        verify(memberService, never()).withdrawMember(any());
        verify(authService, never()).invalidateAllTokens(any());
    }

    @Test
    @DisplayName("사용자 정의 사유 없이 일반 사유로 탈퇴 성공")
    void withdrawMemberWithReason_WithoutCustomReason_Success() {
        // given
        String memberKey = "google 123456";
        WithdrawRequestDto request = new WithdrawRequestDto(
            WithdrawReasonType.PRIVACY_CONCERN,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(withdrawReasonRepository).save(argThat(reason -> 
            reason.getReasonType() == WithdrawReasonType.PRIVACY_CONCERN &&
            reason.getCustomReason() == null &&
            reason.getMemberKey().equals(memberKey)
        ));
        verify(memberService).withdrawMember(member.getId());
        verify(authService).invalidateAllTokens(memberKey);
    }
} 