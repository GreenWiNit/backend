package com.example.green.domain.auth.service;

import java.util.Optional;

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
import com.example.green.domain.auth.exception.WithdrawnMemberAccessException;
import com.example.green.domain.member.entity.Member;
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
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		log.info("loadUser : {}", oAuth2User);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuth2ResponseDto oAuth2Response = OAuth2Provider.of(registrationId)
			.createResponse(oAuth2User.getAttributes());

		String memberKey = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

		OAuth2UserInfoDto oauth2UserInfoDto = new OAuth2UserInfoDto(
			oAuth2Response.getEmail(),
			oAuth2Response.getName(),
			null, // profile image URL (구글은 picture 필드 사용 - 추후 구현)
			oAuth2Response.getProvider(),
			oAuth2Response.getProviderId()
		);

		Optional<Member> existingMember = memberService.findByMemberKey(memberKey);
		
		// 신규 사용자 처리
		if (existingMember.isEmpty()) {
			log.info("신규 사용자 발견: {}", memberKey);
			UserDto userDto = UserDto.forNewUser(oauth2UserInfoDto);
			return new CustomOAuth2UserDto(userDto);
		}
		
		Member member = existingMember.get();

		if (member.isWithdrawn()) {
			log.warn("탈퇴한 회원의 재가입 시도 차단: {}", memberKey);
			throw new WithdrawnMemberAccessException(memberKey);
		}

		return updateExistingUser(memberKey, oAuth2Response);
	}

	@Transactional
	public CustomOAuth2UserDto updateExistingUser(String memberKey, OAuth2ResponseDto oAuth2Response) {
		log.info("기존 사용자 로그인: {}", memberKey);

		// Member 도메인 서비스에 위임하여 OAuth2 정보 업데이트
		memberService.updateOAuth2Info(memberKey, oAuth2Response.getName(), oAuth2Response.getEmail());

		UserDto userDto = UserDto.forExistingUser(
			"ROLE_USER",
			oAuth2Response.getName(),
			memberKey
		);

		log.debug("OAuth2 사용자 정보 업데이트 완료: {}", memberKey);
		return new CustomOAuth2UserDto(userDto);
	}
}


