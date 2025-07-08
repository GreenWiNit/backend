package com.example.integration.admin;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.admin.entity.Admin;
import com.example.green.domain.admin.entity.enums.AdminStatus;
import com.example.green.domain.admin.repository.AdminRepository;
import com.example.integration.common.BaseIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
class AdminAuthIntegrationTest extends BaseIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ObjectMapper objectMapper;

	private Admin testAdmin;

	@BeforeEach
	void setUp() {
		// 테스트용 어드민 계정 생성
		testAdmin = createTestAdmin("admin1234", "admin1234!", "관리자", "admin@test.com");
		adminRepository.save(testAdmin);
	}

	@Test
	@DisplayName("올바른 어드민 로그인 전체 플로우 테스트")
	void 올바른_어드민_로그인_전체_플로우_테스트() throws Exception {
		// given
		String loginRequest = """
			{
				"loginId": "admin1234",
				"password": "admin1234!"
			}
			""";

		// when & then
		String responseContent = mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequest))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").exists())
			.andExpect(jsonPath("$.loginId").value("admin1234"))
			.andExpect(jsonPath("$.name").value("관리자"))
			.andExpect(jsonPath("$.role").value("ROLE_ADMIN"))
			.andReturn()
			.getResponse()
			.getContentAsString();

		// JWT 토큰 검증
		JsonNode response = objectMapper.readTree(responseContent);
		String accessToken = response.get("accessToken").asText();
		
		assertThat(accessToken).isNotEmpty();
		assertThat(accessToken).startsWith("eyJ"); // JWT 형식 확인

		// 데이터베이스에서 로그인 시간이 업데이트되었는지 확인
		Admin updatedAdmin = adminRepository.findByLoginId("admin1234").orElseThrow();
		assertThat(updatedAdmin.getLastLoginAt()).isNotNull();
	}

	@Test
	@DisplayName("잘못된 비밀번호로 로그인 실패")
	void 잘못된_비밀번호로_로그인_실패() throws Exception {
		// given
		String loginRequest = """
			{
				"loginId": "admin1234",
				"password": "wrongpassword"
			}
			""";

		// when & then
		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequest))
			.andDo(print())
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));

		// 로그인 실패 시 로그인 시간이 업데이트되지 않아야 함
		Admin admin = adminRepository.findByLoginId("admin1234").orElseThrow();
		assertThat(admin.getLastLoginAt()).isNull();
	}

	@Test
	@DisplayName("존재하지 않는 계정으로 로그인 실패")
	void 존재하지_않는_계정으로_로그인_실패() throws Exception {
		// given
		String loginRequest = """
			{
				"loginId": "nonexistent",
				"password": "admin1234!"
			}
			""";

		// when & then
		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequest))
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("해당 관리자를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("잠긴 계정으로 로그인 실패")
	void 잠긴_계정으로_로그인_실패() throws Exception {
		// given
		Admin lockedAdmin = createTestAdmin("locked", "admin1234!", "잠긴관리자", "locked@test.com");
		// status를 LOCKED로 변경
		setAdminStatus(lockedAdmin, AdminStatus.LOCKED);
		adminRepository.save(lockedAdmin);

		String loginRequest = """
			{
				"loginId": "locked",
				"password": "admin1234!"
			}
			""";

		// when & then
		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequest))
			.andDo(print())
			.andExpect(status().isNotFound()) // ACTIVE가 아니므로 NOT_FOUND
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value("해당 관리자를 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("유효성 검증 실패 - 빈 로그인 ID")
	void 유효성_검증_실패_빈_로그인_ID() throws Exception {
		// given
		String loginRequest = """
			{
				"loginId": "",
				"password": "admin1234!"
			}
			""";

		// when & then
		mockMvc.perform(post("/api/admin/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequest))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("로그인 ID를 입력해주세요")));
	}

	private Admin createTestAdmin(String loginId, String password, String name, String email) {
		String encodedPassword = passwordEncoder.encode(password);
		
		// Reflection을 사용해서 private 생성자 호출
		try {
			var constructor = Admin.class.getDeclaredConstructor(String.class, String.class, String.class, String.class);
			constructor.setAccessible(true);
			return constructor.newInstance(loginId, encodedPassword, name, email);
		} catch (Exception e) {
			throw new RuntimeException("테스트용 Admin 생성 실패", e);
		}
	}

	private void setAdminStatus(Admin admin, AdminStatus status) {
		try {
			var statusField = Admin.class.getDeclaredField("status");
			statusField.setAccessible(true);
			statusField.set(admin, status);
		} catch (Exception e) {
			throw new RuntimeException("Admin 상태 설정 실패", e);
		}
	}
} 