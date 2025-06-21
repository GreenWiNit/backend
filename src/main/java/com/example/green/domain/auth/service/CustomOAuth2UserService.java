package com.example.green.domain.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.example.green.domain.auth.dto.GoogleResponseDto;
import com.example.green.domain.auth.dto.OAuth2Response;

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

		//TODO 로그인 이후 로직 추후 구현 예정
	}
}

