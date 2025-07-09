package com.example.green.domain.auth.admin.controller;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.auth.admin.controller.AdminAuthController;
import com.example.green.domain.auth.admin.dto.AdminLoginRequestDto;
import com.example.green.domain.auth.admin.dto.AdminLoginResponseDto;
import com.example.green.domain.auth.admin.entity.Admin;
import com.example.green.domain.auth.admin.exception.AdminExceptionMessage;
import com.example.green.domain.auth.admin.service.AdminService;
import com.example.green.domain.auth.service.TokenService;
import com.example.green.global.error.exception.BusinessException;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(AdminAuthController.class)
class AdminAuthControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private AdminService adminService;

	@MockitoBean
	private TokenService tokenService;

	@Test
	@DisplayName("올바른 로그인 정보로 로그인 성공")
	void 올바른_로그인_정보로_로그인_성공() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("admin1234", "admin1234!");
		Admin mockAdmin = createMockAdmin();
		String expectedToken = "eyJhbGciOiJIUzI1NiJ9...";

		given(adminService.authenticate("admin1234", "admin1234!"))
			.willReturn(mockAdmin);
		given(tokenService.createAccessToken("admin_admin1234", Admin.ROLE_ADMIN))
			.willReturn(expectedToken);

		AdminLoginResponseDto response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {});

		assertThat(response.getAccessToken()).isEqualTo(expectedToken);
		assertThat(response.getLoginId()).isEqualTo("admin1234");
		assertThat(response.getName()).isEqualTo("관리자");
		assertThat(response.getRole()).isEqualTo(Admin.ROLE_ADMIN);
	}

	@Test
	@DisplayName("존재하지 않는 계정으로 로그인 실패")
	void 존재하지_않는_계정으로_로그인_실패() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("nonexistent", "admin1234!");

		given(adminService.authenticate("nonexistent", "admin1234!"))
			.willThrow(new BusinessException(AdminExceptionMessage.ADMIN_NOT_FOUND));

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.NOT_FOUND)
			.body("success", equalTo(false))
			.body("message", equalTo(AdminExceptionMessage.ADMIN_NOT_FOUND.getMessage()));
	}

	@Test
	@DisplayName("잘못된 비밀번호로 로그인 실패")
	void 잘못된_비밀번호로_로그인_실패() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("admin1234", "wrongpassword");

		given(adminService.authenticate("admin1234", "wrongpassword"))
			.willThrow(new BusinessException(AdminExceptionMessage.INVALID_PASSWORD));

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.UNAUTHORIZED)
			.body("success", equalTo(false))
			.body("message", equalTo(AdminExceptionMessage.INVALID_PASSWORD.getMessage()));
	}

	@Test
	@DisplayName("빈 로그인 ID로 요청 시 검증 실패")
	void 빈_로그인_ID로_요청시_검증_실패() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("", "admin1234!");

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("success", equalTo(false))
			.body("errors[0].message", containsString("로그인 ID를 입력해주세요"));
	}

	@Test
	@DisplayName("빈 비밀번호로 요청 시 검증 실패")
	void 빈_비밀번호로_요청시_검증_실패() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("admin1234", "");

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("success", equalTo(false))
			// 두 개의 에러 메시지 중 NotBlank 메시지도 반드시 포함되었는지 검사
			.body("errors.message", hasItem(containsString("비밀번호를 입력해주세요")));
	}

	@Test
	@DisplayName("너무 긴 로그인 ID로 요청 시 검증 실패")
	void 너무_긴_로그인_ID로_요청시_검증_실패() {
		String tooLongLoginId = "a".repeat(51);
		AdminLoginRequestDto request = new AdminLoginRequestDto(tooLongLoginId, "admin1234!");

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("success", equalTo(false))
			.body("errors[0].message", containsString("로그인 ID는 50자 이하로 입력해주세요"));
	}

	@Test
	@DisplayName("너무 짧은 비밀번호로 요청 시 검증 실패")
	void 너무_짧은_비밀번호로_요청시_검증_실패() {
		AdminLoginRequestDto request = new AdminLoginRequestDto("admin1234", "123");

		RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/admin/login")
			.then().log().all()
			.status(HttpStatus.BAD_REQUEST)
			.body("success", equalTo(false))
			.body("errors[0].message", containsString("비밀번호는 4자 이상 100자 이하로 입력해주세요"));
	}

	private Admin createMockAdmin() {
		Admin admin = mock(Admin.class);
		given(admin.getLoginId()).willReturn("admin1234");
		given(admin.getName()).willReturn("관리자");
		given(admin.getTokenUsername()).willReturn("admin_admin1234");
		return admin;
	}
}