package com.example.green.domain.member.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 회원 탈퇴 요청 검증 어노테이션
 * 
 * reasonType이 OTHER인 경우 customReason 필수 입력 검증
 */
@Documented
@Constraint(validatedBy = WithdrawRequestValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidWithdrawRequest {
    
    String message() default "기타 사유 선택 시 상세 사유를 입력해주세요.";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
} 