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
	"app.frontend.base-url=http://localhost:3000"
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
			.andExpect(status().isForbidden());
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
} 