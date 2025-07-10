package com.example.green.domain.auth.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.green.domain.auth.service.AuthService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.domain.member.entity.Member;
import com.example.green.domain.member.exception.MemberExceptionMessage;
import com.example.green.domain.member.service.MemberService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.global.security.PrincipalDetails;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AuthService authService;

	@MockBean
	private TokenService tokenService;

	@MockBean
	private MemberService memberService;

	private Member testMember;

	@BeforeEach
	void setUp() {
		testMember = Member.create("google 123", "테스트회원", "test@example.com");
	}

	@Test
	@DisplayName("로그인하지 않은 사용자가 회원 탈퇴 요청 시 401 응답")
	void withdrawMember_WithoutAuthentication_ShouldReturn401() throws Exception {
		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isUnauthorized());
		
		// 서비스 메서드 호출되지 않음
		verify(authService, never()).withdrawMember(any());
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("로그인한 사용자가 회원 탈퇴 요청 시 성공")
	void withdrawMember_WithAuthentication_ShouldSucceed() throws Exception {
		// given
		String username = "google 123";

		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.message").value("회원 탈퇴가 완료되었습니다."));

		// 서비스 메서드 호출 확인
		verify(authService).withdrawMember(username);
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("존재하지 않는 회원 탈퇴 요청 시 404 응답")
	void withdrawMember_WithNonExistentUser_ShouldReturn404() throws Exception {
		// given
		String username = "google 123";
		doThrow(new BusinessException(MemberExceptionMessage.MEMBER_NOT_FOUND))
			.when(authService).withdrawMember(username);

		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("해당 회원을 찾을 수 없습니다."));

		verify(authService).withdrawMember(username);
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("이미 탈퇴한 회원 탈퇴 요청 시 400 응답")
	void withdrawMember_WithAlreadyWithdrawnUser_ShouldReturn400() throws Exception {
		// given
		String username = "google 123";
		doThrow(new BusinessException(MemberExceptionMessage.MEMBER_ALREADY_WITHDRAWN))
			.when(authService).withdrawMember(username);

		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("이미 탈퇴한 회원입니다."));

		verify(authService).withdrawMember(username);
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("탈퇴 처리 중 서버 오류 발생 시 500 응답")
	void withdrawMember_WithServerError_ShouldReturn500() throws Exception {
		// given
		String username = "google 123";
		doThrow(new RuntimeException("Internal server error"))
			.when(authService).withdrawMember(username);

		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isInternalServerError());

		verify(authService).withdrawMember(username);
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("GET 방식으로 회원 탈퇴 요청 시 405 응답")
	void withdrawMember_WithGetMethod_ShouldReturn405() throws Exception {
		// when & then
		mockMvc.perform(get("/api/auth/withdraw"))
			.andExpect(status().isMethodNotAllowed());
		
		// 서비스 메서드 호출되지 않음
		verify(authService, never()).withdrawMember(any());
	}

	@Test
	@WithMockUser(username = "google 123")
	@DisplayName("회원 탈퇴 성공 시 RefreshToken 쿠키 제거")
	void withdrawMember_ShouldRemoveRefreshTokenCookie() throws Exception {
		// given
		String username = "google 123";

		// when & then
		mockMvc.perform(post("/api/auth/withdraw"))
			.andExpect(status().isOk())
			.andExpect(cookie().value("refreshToken", ""))
			.andExpect(cookie().maxAge("refreshToken", 0))
			.andExpect(cookie().path("refreshToken", "/"));

		verify(authService).withdrawMember(username);
	}
} 