package com.example.green.domain.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.example.green.domain.auth.dto.CustomOAuth2User;
import com.example.green.domain.auth.dto.GoogleResponseDto;
import com.example.green.domain.auth.dto.OAuth2Response;
import com.example.green.domain.auth.dto.OAuth2UserInfo;
import com.example.green.domain.auth.dto.UserDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		log.info("loadUser : {}", oAuth2User);

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

		// OAuth2 사용자 정보 생성
		OAuth2UserInfo oauth2UserInfo = new OAuth2UserInfo(
			oAuth2Response.getEmail(),
			oAuth2Response.getName(),
			null, // profile image URL (구글은 picture 필드 사용 - 추후 구현)
			oAuth2Response.getProvider(),
			oAuth2Response.getProviderId()
		);

		Member existData = memberRepository.findByUsername(username);

		if (existData == null) {
			// 신규 사용자 - 임시 토큰 생성 후 회원가입 페이지로 리다이렉트 필요
			log.info("신규 사용자 발견: {}", username);
			
			UserDto userDto = UserDto.forNewUser(oauth2UserInfo);
			return new CustomOAuth2User(userDto);
		} else {
			// 기존 사용자 - 정보 업데이트 후 정상 로그인 처리
			log.info("기존 사용자 로그인: {}", username);
			
			// name과 email 정보만 업데이트
			existData.updateOAuth2Info(oAuth2Response.getName(), oAuth2Response.getEmail());
			memberRepository.save(existData);

			UserDto userDto = UserDto.forExistingUser(
				"ROLE_" + existData.getRole().name(),
				oAuth2Response.getName(),
				existData.getUsername()
			);

			return new CustomOAuth2User(userDto);
		}
	}
}


