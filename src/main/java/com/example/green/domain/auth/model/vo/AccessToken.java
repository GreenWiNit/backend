package com.example.green.domain.auth.model.vo;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AccessToken Value Object
 */
@Getter
@EqualsAndHashCode(of = "tokenValue")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessToken {

	private final String tokenValue;
	private final TokenService tokenService;

	/**
	 * 정적 팩토리 메서드 - AccessToken 생성
	 */
	public static AccessToken from(String tokenValue, TokenService tokenService) {
		if (tokenValue == null || tokenValue.trim().isEmpty()) {
			throw new IllegalArgumentException("AccessToken 값은 null이거나 비어있을 수 없습니다.");
		}
		return new AccessToken(tokenValue.trim(), tokenService);
	}

	/**
	 * 토큰 유효성 검증
	 */
	public boolean isValid() {
		try {
			return tokenService.validateToken(tokenValue) && isAccessTokenType();
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isAccessTokenType() {
		try {
			String tokenType = tokenService.getTokenType(tokenValue);
			return TOKEN_TYPE_ACCESS.equals(tokenType);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 토큰에서 사용자명 추출
	 */
	public String getUsername() {
		validateTokenBeforeExtraction();
		return tokenService.getUsername(tokenValue);
	}

	/**
	 * 토큰에서 권한 추출
	 */
	public String getRole() {
		validateTokenBeforeExtraction();
		return tokenService.getRole(tokenValue);
	}

	/**
	 * Authorization Bearer 헤더 형식으로 변환
	 */
	public String toBearerHeader() {
		return "Bearer " + tokenValue;
	}

	/**
	 * 원본 토큰 문자열 반환 (필요한 경우에만)
	 */
	public String getValue() {
		return tokenValue;
	}

	private void validateTokenBeforeExtraction() {
		if (!isValid()) {
			throw new BusinessException(GlobalExceptionMessage.JWT_VALIDATION_FAILED);
		}
	}

	/**
	 * toString에서는 토큰 값을 일부만 노출하도록 마스킹 처리
	 */
	@Override
	public String toString() {
		String masked = tokenValue.length() > 20
			? tokenValue.substring(0, 20) + "..."
			: tokenValue;
		return "AccessToken{tokenValue='" + masked + "'}";
	}
}
