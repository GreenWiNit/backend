package com.example.green.domain.member.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.member.dto.WithdrawRequestDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.WithdrawReason;
import com.example.green.domain.member.entity.enums.WithdrawReasonType;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.repository.MemberRepository;
import com.example.green.domain.member.repository.WithdrawReasonRepository;
import com.example.green.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 탈퇴 전담 서비스
 * 
 * 탈퇴 관련 비즈니스 로직을 담당하며, 탈퇴 사유 저장과 함께
 * 회원 탈퇴 처리를 수행합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawService {

    private final MemberRepository memberRepository;
    private final WithdrawReasonRepository withdrawReasonRepository;
    private final MemberService memberService;
    private final AuthService authService;

    // 재시도 설정 상수
    private static final int MAX_ATTEMPTS = 3;
    private static final int DELAY = 100;
    private static final double MULTIPLIER = 2.0;

    /**
     * 탈퇴 사유와 함께 회원 탈퇴 처리
     * 
     * @param memberKey 탈퇴할 회원의 memberKey
     * @param withdrawRequest 탈퇴 요청 정보 (사유 포함)
     */
    @Retryable(
        retryFor = {OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class},
        maxAttempts = MAX_ATTEMPTS,
        backoff = @Backoff(delay = DELAY, multiplier = MULTIPLIER, random = true)
    )
    public void withdrawMemberWithReason(String memberKey, WithdrawRequestDto withdrawRequest) {
        log.info("[WITHDRAW] 탈퇴 사유와 함께 회원 탈퇴 처리 시작 - memberKey: {}, reasonType: {}", 
                 memberKey, withdrawRequest.reasonType());

        Member member = findMemberByMemberKey(memberKey);
        validateMemberCanWithdraw(member);

        try {
            saveWithdrawReason(member, withdrawRequest);

            memberService.withdrawMember(member.getId());

            authService.invalidateAllTokens(memberKey);

            log.info("[WITHDRAW] 탈퇴 사유 저장 및 회원 탈퇴 완료 - memberKey: {}, reasonType: {}", 
                     memberKey, withdrawRequest.reasonType());

        } catch (Exception e) {
            log.error("[WITHDRAW] 회원 탈퇴 처리 중 오류 발생 - memberKey: {}", memberKey, e);
            throw new BusinessException(MemberExceptionMessage.MEMBER_WITHDRAW_FAILED);
        }
    }

    /**
     * 탈퇴 사유 저장
     */
    private void saveWithdrawReason(Member member, WithdrawRequestDto withdrawRequest) {
        WithdrawReasonType reasonType = withdrawRequest.reasonType();
        String customReason = withdrawRequest.getCleanCustomReason();

        // OTHER 타입인 경우 사용자 정의 사유 필수
        validateCustomReasonIfNeeded(reasonType, customReason);

        WithdrawReason withdrawReason = WithdrawReason.create(member, reasonType, customReason);
        withdrawReasonRepository.save(withdrawReason);

        log.info("[WITHDRAW] 탈퇴 사유 저장 완료 - memberKey: {}, reasonType: {}, hasCustomReason: {}", 
                 member.getMemberKey(), reasonType, withdrawReason.hasCustomReason());
    }

    /**
     * 회원 조회
     */
    private Member findMemberByMemberKey(String memberKey) {
        return memberRepository.findByMemberKey(memberKey)
            .orElseThrow(() -> {
                log.error("[WITHDRAW] 회원을 찾을 수 없음 - memberKey: {}", memberKey);
                return new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND);
            });
    }

    /**
     * 탈퇴 가능한 회원인지 검증
     */
    private void validateMemberCanWithdraw(Member member) {
        if (member.isWithdrawn()) {
            log.warn("[WITHDRAW] 이미 탈퇴한 회원입니다 - memberKey: {}", member.getMemberKey());
            throw new BusinessException(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN);
        }
    }

    /**
     * OTHER 타입인 경우 사용자 정의 사유 필수 검증
     */
    private void validateCustomReasonIfNeeded(WithdrawReasonType reasonType, String customReason) {
        if (reasonType.isCustomReason() && (customReason == null || customReason.trim().isEmpty())) {
            log.warn("[WITHDRAW] 기타 사유 선택 시 상세 사유 입력 필수");
            throw new BusinessException(MemberExceptionMessage.WITHDRAW_CUSTOM_REASON_REQUIRED);
        }
    }
} 