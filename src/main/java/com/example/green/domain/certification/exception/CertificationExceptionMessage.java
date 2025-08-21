package com.example.green.domain.certification.exception;

import org.springframework.http.HttpStatus;

import com.example.green.global.error.exception.ExceptionMessage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CertificationExceptionMessage implements ExceptionMessage {

	EXISTS_CHALLENGE_CERT_OF_DAY(HttpStatus.BAD_REQUEST, "오늘은 이미 해당 챌린지를 인증했어요! 디른 챌린지에 도전해보세요."),
	FUTURE_DATE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "인증 날짜는 미래 날짜를 선택할 수 없습니다."),
	NOT_FOUND_CHALLENGE_CERTIFICATION(HttpStatus.BAD_REQUEST, "챌린지 인증 정보를 찾을 수 없습니다."),
	INVALID_CHALLENGE_TYPE(HttpStatus.BAD_REQUEST, "챌린지 유형을 명확히 해주세요. (T or P)"),
	INVALID_ACCESS(HttpStatus.UNAUTHORIZED, "챌린지 인증은 본인 것만 확인할 수 있습니다."),
	ALREADY_APPROVED_CERT(HttpStatus.BAD_REQUEST, "이미 포인트가 지급된 인증은 미지급 처리 할 수 없습니다."),
	CHALLENGE_NOT_COMPLETED_YET(HttpStatus.BAD_REQUEST, "챌린지 활동이 완료된 후에 인증 가능합니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
