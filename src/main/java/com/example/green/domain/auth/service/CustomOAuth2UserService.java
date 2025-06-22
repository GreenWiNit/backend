package com.example.green.domain.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.dto.GoogleResponseDto;
import com.example.green.domain.auth.dto.OAuth2Response;
import com.example.green.domain.auth.dto.UserDto;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		System.out.println(oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		OAuth2Response oAuth2Response = null;

		if (registrationId.equals("google")) {
			oAuth2Response = new GoogleResponseDto(oAuth2User.getAttributes());

		} else if (registrationId.equals("naver")) {
			//TODO 구현 예정

		} else if (registrationId.equals("kakao")) {
			//TODO 구현 예정

		} else {
			return null;
		}

		// 리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만든다.
		String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		UserDto userDto = new UserDto(
			"ROLE_USER",
			oAuth2Response.getName(),
			username
		);

		return new CustomOAuth2User(userDto);

	}
}


