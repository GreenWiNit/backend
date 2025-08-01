package com.example.green.domain.member.dto.validation;

import com.example.green.domain.member.dto.WithdrawRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 회원 탈퇴 요청 검증기
 * 
 * reasonType이 OTHER인 경우 customReason 필수 입력 검증
 */
public class WithdrawRequestValidator implements ConstraintValidator<ValidWithdrawRequest, WithdrawRequestDto> {

    @Override
    public void initialize(ValidWithdrawRequest constraintAnnotation) {
        // 초기화 로직 (필요한 경우)
    }

    @Override
    public boolean isValid(WithdrawRequestDto withdrawRequest, ConstraintValidatorContext context) {
        if (withdrawRequest == null) {
            return true; // null 검증은 @NotNull에서 처리
        }

        // reasonType이 OTHER인 경우 customReason 필수 검증
        if (withdrawRequest.reasonType() != null && 
            withdrawRequest.reasonType().isCustomReason()) {
            
            String customReason = withdrawRequest.customReason();
            return customReason != null && !customReason.trim().isEmpty();
        }

        return true; // OTHER가 아닌 경우는 항상 유효
    }
} 