package com.example.green.domain.member.dto;

import java.util.List;

import com.example.green.domain.member.entity.enums.WithdrawReasonType;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


@Schema(description = "회원 탈퇴 요청")
public record WithdrawRequestDto(
    
    @NotEmpty(message = "탈퇴 사유를 최소 1개 이상 선택해주세요.")
    @Size(min = 1, max = 5, message = "탈퇴 사유는 최소 1개, 최대 5개까지 선택 가능합니다.")
    @ArraySchema(
        schema = @Schema(
            description = "탈퇴 사유 타입 (다중 선택, 최소 1개 ~ 최대 5개)",
            implementation = WithdrawReasonType.class,
            allowableValues = {
                "SERVICE_DISSATISFACTION", 
                "POLICY_DISAGREEMENT", 
                "PRIVACY_CONCERN", 
                "PRIVACY_PROTECTION", 
                "OTHER"
            }
        )
    )
    List<WithdrawReasonType> reasonTypes,
    
    @Size(max = 1000, message = "탈퇴 사유는 1000자 이내로 입력해주세요.")
    @Schema(
        description = "상세 탈퇴 사유 (reasonTypes에 'OTHER'가 포함된 경우 필수 입력)", 
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
    
    public boolean containsOtherReason() {
        return reasonTypes != null && reasonTypes.contains(WithdrawReasonType.OTHER);
    }
} 