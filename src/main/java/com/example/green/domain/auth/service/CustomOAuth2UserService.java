package com.example.green.domain.auth.service;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
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
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.repository.MemberRepository;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

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

		Member existData = memberRepository.findByUsername(username);

		if (existData == null) {
			log.info("신규 사용자 발견: {}", username);
			UserDto userDto = UserDto.forNewUser(oauth2UserInfoDto);
			return new CustomOAuth2UserDto(userDto);
		} else {
			// 낙관적 락 + 재시도로 동시성 처리
			return updateExistingUserWithRetry(existData, oAuth2Response);
		}
	}

	@Retryable(
		retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 100, multiplier = 2.0, random = true)
	)
	@Transactional // 별도 트랜잭션으로 재시도
	public CustomOAuth2UserDto updateExistingUserWithRetry(Member member, OAuth2ResponseDto oAuth2Response) {
		log.info("기존 사용자 로그인 (낙관적 락 + 재시도): {}", member.getUsername());

		// 최신 데이터 재조회 (재시도 시 필요)
		Member latestMember = memberRepository.findByUsername(member.getUsername());
		if (latestMember == null) {
			throw new OAuth2AuthenticationException("사용자를 찾을 수 없습니다: " + member.getUsername());
		}

		//낙관적 락: @Version 체크로 동시성 보장, 실패 시 @Retry가 재시도
		latestMember.updateOAuth2Info(oAuth2Response.getName(), oAuth2Response.getEmail());

		UserDto userDto = UserDto.forExistingUser(
			"ROLE_" + latestMember.getRole().name(),
			oAuth2Response.getName(),
			latestMember.getUsername()
		);

		log.debug("OAuth2 사용자 정보 업데이트 완료 (낙관적 락 + 재시도): {}", member.getUsername());
		return new CustomOAuth2UserDto(userDto);
	}
}


