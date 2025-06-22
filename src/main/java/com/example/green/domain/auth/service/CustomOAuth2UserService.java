package com.example.green.domain.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.dto.GoogleResponseDto;
import com.example.green.domain.auth.dto.OAuth2Response;
import com.example.green.domain.auth.dto.UserDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

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

		Member existData = memberRepository.findByUsername(username);

		if (existData == null) {
			// 새로운 회원 생성
			Member member = Member.createOAuth2Member(
				username,
				oAuth2Response.getName(),
				oAuth2Response.getEmail()
			);
			memberRepository.save(member);

			UserDto userDto = new UserDto(
				"ROLE_USER",
				oAuth2Response.getName(),
				username
			);

			return new CustomOAuth2User(userDto);
		} else {
			// name과 email 정보만 업데이트
			existData.updateOAuth2Info(oAuth2Response.getName(), oAuth2Response.getEmail());
			memberRepository.save(existData);

			UserDto userDto = new UserDto(
				"ROLE_" + existData.getRole().name(),
				oAuth2Response.getName(),
				existData.getUsername()
			);

			return new CustomOAuth2User(userDto);
		}
	}
}


