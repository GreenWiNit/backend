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
        log.info("[WITHDRAW] 탈퇴 사유와 함께 회원 탈퇴 처리 시작 - memberKey: {}, reasonTypes: {}", 
                 memberKey, withdrawRequest.reasonTypes());

        Member member = findMemberByMemberKey(memberKey);
        validateMemberCanWithdraw(member);
        validateWithdrawRequest(withdrawRequest);

        try {
            saveWithdrawReasons(member, withdrawRequest);

            memberService.withdrawMember(member.getId());

            authService.invalidateAllTokens(memberKey);

            log.info("[WITHDRAW] 탈퇴 사유 저장 및 회원 탈퇴 완료 - memberKey: {}, reasonTypes: {}", 
                     memberKey, withdrawRequest.reasonTypes());

        } catch (BusinessException e) {
            log.warn("[WITHDRAW] 비즈니스 검증 실패 - memberKey: {}, message: {}", memberKey, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("[WITHDRAW] 시스템 오류 발생 - memberKey: {}", memberKey, e);
            throw new BusinessException(MemberExceptionMessage.MEMBER_WITHDRAW_FAILED);
        }
    }

    /**
     * 탈퇴 사유들 저장 (다중 선택)
     */
    private void saveWithdrawReasons(Member member, WithdrawRequestDto withdrawRequest) {
        String customReason = withdrawRequest.containsOtherReason() ? 
            withdrawRequest.getCleanCustomReason() : null;
        
        for (WithdrawReasonType reasonType : withdrawRequest.reasonTypes()) {
            // OTHER 타입인 경우에만 customReason 저장
            String reasonCustomText = reasonType.isCustomReason() ? customReason : null;
            
            WithdrawReason withdrawReason = WithdrawReason.create(member, reasonType, reasonCustomText);
            withdrawReasonRepository.save(withdrawReason);
            
            log.info("[WITHDRAW] 탈퇴 사유 저장 - memberKey: {}, reasonType: {}, hasCustomReason: {}", 
                     member.getMemberKey(), reasonType, withdrawReason.hasCustomReason());
        }
        
        log.info("[WITHDRAW] 탈퇴 사유 저장 완료 - memberKey: {}, 총 사유 개수: {}", 
                 member.getMemberKey(), withdrawRequest.reasonTypes().size());
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
     * 탈퇴 요청 검증
     */
    private void validateWithdrawRequest(WithdrawRequestDto withdrawRequest) {
        // OTHER가 포함된 경우 customReason 필수
        if (withdrawRequest.containsOtherReason() && !withdrawRequest.hasCustomReason()) {
            log.warn("[WITHDRAW] 기타 사유 선택 시 상세 사유 입력 필수");
            throw new BusinessException(MemberExceptionMessage.WITHDRAW_CUSTOM_REASON_REQUIRED);
        }
        
        // 중복 사유 검증
        if (withdrawRequest.reasonTypes().size() != withdrawRequest.reasonTypes().stream().distinct().count()) {
            log.warn("[WITHDRAW] 중복된 탈퇴 사유가 포함되어 있습니다.");
            throw new BusinessException(MemberExceptionMessage.DUPLICATE_WITHDRAW_REASONS);
        }
    }
} 