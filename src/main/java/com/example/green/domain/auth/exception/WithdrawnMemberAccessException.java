package com.example.green.domain.auth.exception;

import com.example.green.global.error.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WithdrawnMemberAccessException extends BusinessException {
    
    public WithdrawnMemberAccessException() {
        super(AuthExceptionMessage.WITHDRAWN_MEMBER_ACCESS_DENIED);
    }
    
    public WithdrawnMemberAccessException(String memberKey) {
        super(AuthExceptionMessage.WITHDRAWN_MEMBER_ACCESS_DENIED);
        log.warn("탈퇴한 회원의 재가입 시도: {}", memberKey);
    }
} 