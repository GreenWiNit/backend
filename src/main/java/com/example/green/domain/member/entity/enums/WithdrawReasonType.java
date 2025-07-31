package com.example.green.domain.member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 회원 탈퇴 사유 타입
 * 
 * 서비스 개선을 위한 탈퇴 사유 분류
 */
@Getter
@RequiredArgsConstructor
public enum WithdrawReasonType {
    
    SERVICE_DISSATISFACTION("서비스 이용이 불편해요"),
    POLICY_DISAGREEMENT("원하는 정보가 없어요"),
    PRIVACY_CONCERN("다른 서비스를 이용할 예정이에요"),
    LACK_OF_FEATURES("개인정보 보호를 위해 탈퇴할게요"),
    INFREQUENT_USE("기타"),
    OTHER("기타");

    private final String description;

    /**
     * 사용자 정의 사유인지 확인
     */
    public boolean isCustomReason() {
        return this == OTHER;
    }
} 