package com.example.green.domain.auth.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.auth.dto.CustomOAuth2UserDto;
import com.example.green.domain.auth.dto.OAuth2ResponseDto;
import com.example.green.domain.auth.dto.OAuth2UserInfoDto;
import com.example.green.domain.auth.dto.UserDto;
import com.example.green.domain.auth.enums.OAuth2Provider;
import com.example.green.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberService memberService;

	@Override
	@Transactional // OAuth2 사용자 로딩 시 쓰기 트랜잭션 필요
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("loadUser : {}", oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2ResponseDto oAuth2Response = OAuth2Provider.of(registrationId)
			.createResponse(oAuth2User.getAttributes());

		String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		OAuth2UserInfoDto oauth2UserInfoDto = new OAuth2UserInfoDto(
			oAuth2Response.getEmail(),
			oAuth2Response.getName(),
			null, // profile image URL (구글은 picture 필드 사용 - 추후 구현)
			oAuth2Response.getProvider(),
			oAuth2Response.getProviderId()
		);

		boolean isExistingUser = memberService.existsByUsername(username);

		if (!isExistingUser) {
			log.info("신규 사용자 발견: {}", username);
			UserDto userDto = UserDto.forNewUser(oauth2UserInfoDto);
			return new CustomOAuth2UserDto(userDto);
		} else {
			// 기존 사용자 정보 업데이트 (Member 도메인에 위임)
			return updateExistingUser(username, oAuth2Response);
		}
	}

	@Transactional // 별도 트랜잭션으로 재시도
	public CustomOAuth2UserDto updateExistingUser(String username, OAuth2ResponseDto oAuth2Response) {
		log.info("기존 사용자 로그인: {}", username);

		// Member 도메인 서비스에 위임하여 OAuth2 정보 업데이트
		memberService.updateOAuth2Info(username, oAuth2Response.getName(), oAuth2Response.getEmail());

		UserDto userDto = UserDto.forExistingUser(
			"ROLE_USER",
			oAuth2Response.getName(),
			username
		);

		log.debug("OAuth2 사용자 정보 업데이트 완료: {}", username);
		return new CustomOAuth2UserDto(userDto);
	}
}


