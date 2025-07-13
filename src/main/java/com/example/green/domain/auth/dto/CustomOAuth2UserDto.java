package com.example.green.domain.auth.dto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomOAuth2UserDto implements OAuth2User {

	private final UserDto userDto;

	@Override
	public Map<String, Object> getAttributes() {
		// Google과 Naver, Kakao가 가지는 attribute가 달라 사용하지 않을 예정
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(() -> userDto.role());
	}

	@Override
	public String getName() {
		return userDto.name();
	}

	public String getMemberKey() {
		return userDto.memberKey();
	}
}
