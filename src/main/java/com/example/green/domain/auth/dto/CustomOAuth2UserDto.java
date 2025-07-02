package com.example.green.domain.auth.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserDto implements OAuth2User {

	private final UserDto userDto;

	public CustomOAuth2UserDto(UserDto userDto) {

		this.userDto = userDto;
	}

	@Override
	public Map<String, Object> getAttributes() {
		//google과 naver, kakao가 가지는 attribute가 달라 사용하지 않을 예정
		return null;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Collection<GrantedAuthority> collection = new ArrayList<>();

		collection.add((GrantedAuthority)() -> userDto.role());

		return collection;
	}

	@Override
	public String getName() {

		return userDto.name();
	}

	public String getUsername() {

		return userDto.username();
	}

	public UserDto getUserDto() {
		return userDto;
	}
}
