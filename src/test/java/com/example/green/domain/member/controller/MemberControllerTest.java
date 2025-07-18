package com.example.green.domain.member.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.member.dto.ProfileUpdateRequestDto;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.security.PrincipalDetails;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임과 프로필 이미지 URL")
	void updateProfile_Success() throws Exception {
		// given
		String nickname = "새로운닉네임";
		String profileImageUrl = "https://example.com/new-image.jpg";
		ProfileUpdateRequestDto request = new ProfileUpdateRequestDto(nickname, profileImageUrl);

		Member updatedMember = createMockMember(nickname, profileImageUrl);
		given(memberService.updateProfile(eq(1L), eq(nickname), eq(profileImageUrl)))
			.willReturn(updatedMember);

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자", "test@email.com");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		// when & then
		mockMvc.perform(put("/api/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(auth)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("프로필이 성공적으로 수정되었습니다."))
			.andExpect(jsonPath("$.result.nickname").value(nickname))
			.andExpect(jsonPath("$.result.profileImageUrl").value(profileImageUrl));
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임만")
	void updateProfile_OnlyNickname_Success() throws Exception {
		// given
		String nickname = "새로운닉네임";
		ProfileUpdateRequestDto request = new ProfileUpdateRequestDto(nickname, null);

		Member updatedMember = createMockMember(nickname, null);
		given(memberService.updateProfile(eq(1L), eq(nickname), isNull()))
			.willReturn(updatedMember);

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자", "test@email.com");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		// when & then
		mockMvc.perform(put("/api/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(auth)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.nickname").value(nickname))
			.andExpect(jsonPath("$.result.profileImageUrl").doesNotExist());
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 인증되지 않은 사용자")
	void updateProfile_Unauthorized() throws Exception {
		ProfileUpdateRequestDto request = new ProfileUpdateRequestDto("닉네임", null);

		mockMvc.perform(put("/api/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf()))
			.andExpect(status().isFound());  // 302 리다이렉트
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 닉네임 누락")
	void updateProfile_MissingNickname() throws Exception {
		ProfileUpdateRequestDto request = new ProfileUpdateRequestDto(null, "https://example.com/image.jpg");

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자", "test@email.com");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		mockMvc.perform(put("/api/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(csrf())
				.with(authentication(auth)))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 잘못된 JSON 형식")
	void updateProfile_InvalidJson() throws Exception {
		String invalidJson = "{ invalid json }";

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자", "test@email.com");
		Authentication auth = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		mockMvc.perform(put("/api/members/profile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(invalidJson)
				.with(csrf())
				.with(authentication(auth)))
			.andExpect(status().isBadRequest());
	}

	private Member createMockMember(String nickname, String profileImageUrl) {
		Member member = mock(Member.class);
		Profile profile = mock(Profile.class);

		given(member.getProfile()).willReturn(profile);
		given(profile.getNickname()).willReturn(nickname);
		given(profile.getProfileImageUrl()).willReturn(profileImageUrl);

		return member;
	}
}