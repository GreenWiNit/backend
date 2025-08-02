package com.example.integration.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.integration.common.ServiceIntegrationTest;

@TestPropertySource(properties = {
	"jwt.secret=test-jwt-secret-key-for-testing-purpose-only",
	"jwt.expiration=3600000",
	"app.frontend.base-url=http://localhost:3000",
	"app.backend.base-url=http://localhost:8080"
})
class SecurityAnnotationTest extends ServiceIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("@PublicApi - 인증 없이 접근 가능")
	void shouldAllowAccessToPublicApiWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/api/posts/1"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("@AuthenticatedApi - 인증 없이 접근 불가")
	void shouldDenyAccessToAuthenticatedApiWithoutAuthentication() throws Exception {
		mockMvc.perform(post("/api/posts")
				.contentType("application/json")
				.content(
					"{\"title\":\"Valid Title Here\",\"content\":\"This is a valid content that meets the minimum length requirement.\",\"challengeId\":1}"))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}

	@Test
	@WithMockUser(roles = "USER")
	@DisplayName("@AuthenticatedApi - 인증된 사용자 접근 가능")
	void shouldAllowAccessToAuthenticatedApiWithAuthentication() throws Exception {
		mockMvc.perform(post("/api/posts")
				.contentType("application/json")
				.content(
					"{\"title\":\"Valid Title Here\",\"content\":\"This is a valid content that meets the minimum length requirement.\",\"challengeId\":1}"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = "USER")
	@DisplayName("@AdminApi - 일반 사용자 접근 불가")
	void shouldDenyAccessToAdminApiWithUserRole() throws Exception {
		mockMvc.perform(delete("/api/posts/1"))
			.andDo(print())
			.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	@DisplayName("@AdminApi - 관리자 접근 가능")
	void shouldAllowAccessToAdminApiWithAdminRole() throws Exception {
		mockMvc.perform(delete("/api/posts/1"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("@PublicApi - 무효한 토큰이 있어도 접근 가능")
	void shouldAllowAccessToPublicApiWithInvalidToken() throws Exception {
		mockMvc.perform(get("/api/posts/1")
				.header("Authorization", "Bearer invalid-token-here"))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("@PublicApi - 만료된 형식의 토큰이 있어도 접근 가능")
	void shouldAllowAccessToPublicApiWithExpiredToken() throws Exception {
		// 만료된 JWT 토큰 (실제로는 잘못된 형식의 토큰으로 테스트)
		String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.invalid-signature";
		
		mockMvc.perform(get("/api/posts/1")
				.header("Authorization", "Bearer " + expiredToken))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("@AuthenticatedApi - 무효한 토큰으로는 접근 불가")
	void shouldDenyAccessToAuthenticatedApiWithInvalidToken() throws Exception {
		mockMvc.perform(post("/api/posts")
				.header("Authorization", "Bearer invalid-token-here")
				.contentType("application/json")
				.content(
					"{\"title\":\"Valid Title Here\",\"content\":\"This is a valid content that meets the minimum length requirement.\",\"challengeId\":1}"))
			.andDo(print())
			.andExpect(status().isUnauthorized());
	}
} 