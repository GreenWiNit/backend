package com.example.green.domain.member.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.entity.vo.Profile;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.security.PrincipalDetails;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MemberService memberService;

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임과 프로필 이미지")
	void updateProfile_Success() throws Exception {
		// given
		String nickname = "새로운닉네임";
		MockMultipartFile profileImage = new MockMultipartFile(
			"profileImage",
			"test.jpg",
			"image/jpeg",
			"test image content".getBytes()
		);

		Member updatedMember = createMockMember(1L, nickname, "https://example.com/new-image.jpg");
		given(memberService.updateProfile(eq(1L), eq(nickname), any())).willReturn(updatedMember);

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자");
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		// when & then
		mockMvc.perform(multipart(HttpMethod.PUT, "/api/members/profile")
				.file(profileImage)
				.param("nickname", nickname)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf())
				.with(authentication(authentication)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("프로필이 성공적으로 수정되었습니다."))
			.andExpect(jsonPath("$.result.nickname").value(nickname))
			.andExpect(jsonPath("$.result.profileImageUrl").value("https://example.com/new-image.jpg"));
	}

	@Test
	@DisplayName("프로필 업데이트 성공 - 닉네임만")
	void updateProfile_OnlyNickname_Success() throws Exception {
		// given
		String nickname = "새로운닉네임";

		Member updatedMember = createMockMember(1L, nickname, null);
		given(memberService.updateProfile(eq(1L), eq(nickname), isNull())).willReturn(updatedMember);

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자");
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		// when & then
		mockMvc.perform(multipart(HttpMethod.PUT, "/api/members/profile")
				.param("nickname", nickname)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf())
				.with(authentication(authentication)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.result.nickname").value(nickname));
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 인증되지 않은 사용자")
	void updateProfile_Unauthorized() throws Exception {
		// given
		String nickname = "새로운닉네임";

		// when & then (Spring Security 기본값은 302 리다이렉트)
		mockMvc.perform(multipart(HttpMethod.PUT, "/api/members/profile")
				.param("nickname", nickname)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf()))
			.andExpect(status().isFound()); // 302 Redirect
	}

	@Test
	@DisplayName("프로필 업데이트 실패 - 닉네임 누락")
	void updateProfile_MissingNickname() throws Exception {
		// given
		MockMultipartFile profileImage = new MockMultipartFile(
			"profileImage",
			"test.jpg",
			"image/jpeg",
			"test image content".getBytes()
		);

		PrincipalDetails principalDetails = new PrincipalDetails(1L, "testUser", "ROLE_USER", "테스트사용자");
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			principalDetails, null, principalDetails.getAuthorities());

		// when & then
		mockMvc.perform(multipart(HttpMethod.PUT, "/api/members/profile")
				.file(profileImage)
				.contentType(MediaType.MULTIPART_FORM_DATA)
				.with(csrf())
				.with(authentication(authentication)))
			.andExpect(status().isBadRequest());
	}

	private Member createMockMember(Long id, String nickname, String profileImageUrl) {
		Member member = mock(Member.class);
		Profile profile = mock(Profile.class);

		given(member.getId()).willReturn(id);
		given(member.getProfile()).willReturn(profile);
		given(profile.getNickname()).willReturn(nickname);
		given(profile.getProfileImageUrl()).willReturn(profileImageUrl);

		return member;
	}
}