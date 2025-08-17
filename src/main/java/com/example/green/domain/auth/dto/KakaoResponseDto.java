package com.example.green.domain.auth.dto;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoResponseDto implements OAuth2ResponseDto {

	private final Map<String, Object> attribute;

	@Override
	public String getProvider() {
		return "kakao";
	}

	@Override
	public String getProviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attribute.get("kakao_account");
		return kakaoAccount.get("email").toString();
	}

	@Override
	public String getName() {
		Map<String, Object> kakaoAccount = (Map<String, Object>)attribute.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");
		return profile.get("nickname").toString();
	}
}