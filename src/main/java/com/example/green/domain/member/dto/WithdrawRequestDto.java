package com.example.green.domain.member.dto;

import com.example.green.domain.member.entity.enums.WithdrawReasonType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Schema(description = "회원 탈퇴 요청")
public record WithdrawRequestDto(
    
    @NotNull(message = "탈퇴 사유를 선택해주세요.")
    @Schema(
        description = "탈퇴 사유 타입 (단일 선택)", 
        example = "SERVICE_DISSATISFACTION",
        type = "string",
        allowableValues = {
            "SERVICE_DISSATISFACTION", 
            "POLICY_DISAGREEMENT", 
            "PRIVACY_CONCERN", 
            "PRIVACY_PROTECTION", 
            "OTHER"
        },
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    WithdrawReasonType reasonType,
    
    @Size(max = 1000, message = "탈퇴 사유는 1000자 이내로 입력해주세요.")
    @Schema(
        description = "상세 탈퇴 사유 (reasonType이 'OTHER'인 경우 필수 입력)", 
        example = "챌린지 기능이 부족해서 탈퇴합니다.",
        nullable = true
    )
    String customReason
) {
    

    public boolean hasCustomReason() {
        return customReason != null && !customReason.trim().isEmpty();
    }
    

    public String getCleanCustomReason() {
        return hasCustomReason() ? customReason.trim() : null;
    }
} 