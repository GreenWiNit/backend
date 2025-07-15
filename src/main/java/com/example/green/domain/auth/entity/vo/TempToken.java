package com.example.green.domain.auth.entity.vo;

import static com.example.green.domain.auth.constants.AuthConstants.*;

import com.example.green.domain.auth.dto.TempTokenInfoDto;
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
public class TempToken {

	private final String tokenValue;
	private final TokenService tokenService;


	public static TempToken from(String tokenValue, TokenService tokenService) {
		if (tokenValue == null || tokenValue.trim().isEmpty()) {
			throw new BusinessException(GlobalExceptionMessage.TEMP_TOKEN_EMPTY);
		}
		return new TempToken(tokenValue.trim(), tokenService);
	}

	/**
	 * 토큰 유효성 검증
	 */
	public boolean isValid() {
		try {
			return tokenService.validateToken(tokenValue) && isTempTokenType();
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isTempTokenType() {
		try {
			String tokenType = tokenService.getTokenType(tokenValue);
			return TOKEN_TYPE_TEMP.equals(tokenType);
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 토큰에서 사용자 정보 추출
	 */
	public TempTokenInfoDto extractUserInfo() {
		validateTokenBeforeExtraction();
		return tokenService.extractTempTokenInfo(tokenValue);
	}

	public String getEmail() {
		return extractUserInfo().getEmail();
	}

	public String getName() {
		return extractUserInfo().getName();
	}

	public String getProvider() {
		return extractUserInfo().getProvider();
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
		return "TempToken{tokenValue='" + masked + "'}";
	}
}
