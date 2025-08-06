package com.example.green.domain.auth.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthExceptionMessage implements ExceptionMessage {
    
    WITHDRAWN_MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "탈퇴한 회원은 동일한 SNS 계정으로 재가입할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
} 