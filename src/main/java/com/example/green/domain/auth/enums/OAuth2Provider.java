package com.example.green.domain.auth.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import com.example.green.domain.auth.dto.GoogleResponseDto;
import com.example.green.domain.auth.dto.KakaoResponseDto;
import com.example.green.domain.auth.dto.NaverResponseDto;
import com.example.green.domain.auth.dto.OAuth2ResponseDto;

public enum OAuth2Provider {

	GOOGLE("google", GoogleResponseDto::new),
	NAVER("naver", NaverResponseDto::new),
	KAKAO("kakao", KakaoResponseDto::new);

	private final String registrationId;
	private final Function<Map<String, Object>, OAuth2ResponseDto> mapper;

	OAuth2Provider(String registrationId, Function<Map<String, Object>, OAuth2ResponseDto> mapper) {
		this.registrationId = registrationId;
		this.mapper = mapper;
	}

	public static OAuth2Provider of(String registrationId) {
		return Arrays.stream(values())
			.filter(provider -> provider.registrationId.equalsIgnoreCase(registrationId))
			.findFirst()
			.orElseThrow(() -> new OAuth2AuthenticationException(
				"지원하지 않는 OAuth2 공급자: " + registrationId));
	}

	public OAuth2ResponseDto createResponse(Map<String, Object> attributes) {
		return mapper.apply(attributes);
	}

}
