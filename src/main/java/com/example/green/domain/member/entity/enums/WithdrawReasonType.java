package com.example.green.domain.member.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum WithdrawReasonType {
    
    SERVICE_DISSATISFACTION("서비스 이용이 불편해요"),
    POLICY_DISAGREEMENT("원하는 정보가 없어요"),
    PRIVACY_CONCERN("다른 서비스를 이용할 예정이에요"),
    PRIVACY_PROTECTION("개인정보 보호를 위해 탈퇴할게요"),
    OTHER("기타");

    private final String description;

    public boolean isCustomReason() {
        return this == OTHER;
    }
} 