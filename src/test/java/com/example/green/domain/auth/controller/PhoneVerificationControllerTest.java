package com.example.green.domain.auth.controller;

import static com.example.green.domain.auth.controller.message.PhoneVerificationResponseMessage.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.green.domain.auth.controller.dto.PhoneVerificationRequest;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.service.PhoneVerificationService;
import com.example.green.domain.auth.service.result.PhoneVerificationResult;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.NoContent;
import com.example.green.template.base.BaseControllerUnitTest;

import io.restassured.common.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;

@WebMvcTest(PhoneVerificationController.class)
class PhoneVerificationControllerTest extends BaseControllerUnitTest {

	@MockitoBean
	private PhoneVerificationService phoneVerificationService;

	@Test
	void 휴대전화번호로_인증_요청을하면_토큰과_서버_메일_주소를_응답받는다() {
		// given
		PhoneVerificationRequest request = new PhoneVerificationRequest("010-1234-5678");
		PhoneVerificationResult mockResult = new PhoneVerificationResult("TOKEN", "EMAIL");
		when(phoneVerificationService.request(any(PhoneNumber.class))).thenReturn(mockResult);

		// when
		ApiTemplate<PhoneVerificationResult> response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/auth/phone/request")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});

		// then
		assertThat(response.result()).isEqualTo(mockResult);
		assertThat(response.message()).isEqualTo(PHONE_VERIFICATION_REQUEST_SUCCESS.getMessage());
	}

	@Test
	void 휴대전화번호로_인증_확인을_요청하면_인증_성공_응답을_받는다() {
		// given
		PhoneVerificationRequest request = new PhoneVerificationRequest("010-1234-5678");
		PhoneVerificationResult mockResult = new PhoneVerificationResult("TOKEN", "EMAIL");
		when(phoneVerificationService.request(any(PhoneNumber.class))).thenReturn(mockResult);

		// when
		NoContent response = RestAssuredMockMvc
			.given().log().all()
			.contentType(MediaType.APPLICATION_JSON)
			.body(request)
			.when().post("/api/auth/phone/verify")
			.then().log().all()
			.status(HttpStatus.OK)
			.extract().as(new TypeRef<>() {
			});

		// then
		assertThat(response.message()).isEqualTo(PHONE_VERIFICATION_SUCCESS.getMessage());
	}
}
