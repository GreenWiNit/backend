package com.example.green.domain.auth.dto;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NaverResponseDto implements OAuth2ResponseDto {

	private final Map<String, Object> attribute;

	@Override
	public String getProvider() {
		return "naver";
	}

	@Override
	public String getProviderId() {
		Map<String, Object> response = (Map<String, Object>)attribute.get("response");
		return response.get("id").toString();
	}

	@Override
	public String getEmail() {
		Map<String, Object> response = (Map<String, Object>)attribute.get("response");
		return response.get("email").toString();
	}

	@Override
	public String getName() {
		Map<String, Object> response = (Map<String, Object>)attribute.get("response");
		return response.get("name").toString();
	}
}