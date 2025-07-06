package com.example.green.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * OAuth2 임시 토큰에서 추출한 사용자 정보를 담는 DTO
 */
@Getter
@Builder
public class TempTokenInfoDto {

	private final String email;
	private final String name;
	private final String profileImageUrl;
	private final String provider;
	private final String providerId;
}

