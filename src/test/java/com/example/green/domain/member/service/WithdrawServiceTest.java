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
import java.util.Arrays;
import java.util.List;

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
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            WithdrawReasonType.PRIVACY_CONCERN
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(withdrawReasonRepository, times(2)).save(any(WithdrawReason.class));
        verify(memberService).withdrawMember(member.getId());
        verify(authService).invalidateAllTokens(memberKey);
    }

    @Test
    @DisplayName("기타 사유 선택 시 상세 사유 입력 없으면 예외 발생")
    void withdrawMemberWithReason_OtherReasonWithoutCustomReason_ThrowsException() {
        // given
        String memberKey = "google 123456";
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            WithdrawReasonType.OTHER
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
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
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.OTHER
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            "   "
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
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
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
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
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
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
    @DisplayName("다중 사유 선택으로 탈퇴 성공")
    void withdrawMemberWithReason_MultipleReasons_Success() {
        // given
        String memberKey = "google 123456";
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            WithdrawReasonType.PRIVACY_CONCERN,
            WithdrawReasonType.POLICY_DISAGREEMENT
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(withdrawReasonRepository, times(3)).save(any(WithdrawReason.class));
        verify(memberService).withdrawMember(member.getId());
        verify(authService).invalidateAllTokens(memberKey);
    }
    
    @Test
    @DisplayName("중복된 탈퇴 사유 선택 시 예외 발생")
    void withdrawMemberWithReason_DuplicateReasons_ThrowsException() {
        // given
        String memberKey = "google 123456";
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            WithdrawReasonType.PRIVACY_CONCERN,
            WithdrawReasonType.SERVICE_DISSATISFACTION  // 중복
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            null
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when & then
        assertThatThrownBy(() -> withdrawService.withdrawMemberWithReason(memberKey, request))
            .isInstanceOf(BusinessException.class)
            .hasMessage(MemberExceptionMessage.DUPLICATE_WITHDRAW_REASONS.getMessage());
            
        verify(withdrawReasonRepository, never()).save(any());
        verify(memberService, never()).withdrawMember(any());
        verify(authService, never()).invalidateAllTokens(any());
    }
    
    @Test
    @DisplayName("기타 사유와 함께 다른 사유 선택 시 상세 사유 입력으로 성공")
    void withdrawMemberWithReason_OtherWithCustomReason_Success() {
        // given
        String memberKey = "google 123456";
        List<WithdrawReasonType> reasonTypes = Arrays.asList(
            WithdrawReasonType.SERVICE_DISSATISFACTION,
            WithdrawReasonType.OTHER
        );
        WithdrawRequestDto request = new WithdrawRequestDto(
            reasonTypes,
            "기타 상세 사유입니다."
        );
        
        Member member = Member.create(memberKey, "테스트회원", "test@example.com", "테스트닉네임");
        
        given(memberRepository.findByMemberKey(memberKey)).willReturn(Optional.of(member));

        // when
        withdrawService.withdrawMemberWithReason(memberKey, request);

        // then
        verify(withdrawReasonRepository, times(2)).save(any(WithdrawReason.class));
        verify(memberService).withdrawMember(member.getId());
        verify(authService).invalidateAllTokens(memberKey);
    }
} 