package com.example.green.domain.auth.entity.vo;

import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.error.exception.GlobalExceptionMessage;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(of = "tokenValue")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessToken {

	private final String tokenValue;
	private final TokenService tokenService;

	public static AccessToken from(String tokenValue, TokenService tokenService) {
		if (tokenValue == null || tokenValue.trim().isEmpty()) {
			throw new BusinessException(GlobalExceptionMessage.TEMP_TOKEN_EMPTY);
		}
		return new AccessToken(tokenValue.trim(), tokenService);
	}

	/**
	 * 토큰 유효성 검증 (JWT 형식 + tokenVersion 검증)
	 * 로그아웃 후 AccessToken 재사용 방지
	 */
	public boolean isValid() {
		try {
			return tokenService.validateAccessToken(tokenValue);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 토큰에서 사용자명 추출
	 */
	public String getMemberKey() {
		validateTokenBeforeExtraction();
		return tokenService.getMemberKey(tokenValue);
	}

	/**
	 * 토큰에서 권한 추출
	 */
	public String getRole() {
		validateTokenBeforeExtraction();
		return tokenService.getRole(tokenValue);
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
