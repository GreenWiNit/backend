package com.example.green.unit.global.error;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;

import com.example.green.global.error.GlobalExceptionHandler;
import com.example.green.global.error.exception.GlobalExceptionMessage;
import com.example.green.unit.dummy.ErrorDummyController;
import com.example.green.unit.dummy.ValidationDto;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

class ValidationExceptionHandlerTest {

	@BeforeEach
	void setUp() {
		RestAssuredMockMvc.standaloneSetup(
			new ErrorDummyController(),
			new GlobalExceptionHandler()
		);
	}

	@Test
	void RequestBody_유효성_검증이_실패한다() {
		// given
		ValidationDto dto = new ValidationDto(null, null, null);
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_NOT_VALID_MESSAGE;

		// when & then
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when()
			.post("/invalid-request-body")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void ModelAttribute_유효성_검증이_실패한다() {
		// given
		Map<String, Object> params = Map.of("test", "", "age", "");
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_NOT_VALID_MESSAGE;

		// when & then
		given().log().all()
			.params(params)
			.when()
			.get("/invalid-model-attribute")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void ModelAttribute_타입_변환이_실패하면_예외가_발생한다() {
		// given
		Map<String, Object> params = Map.of("test", "test", "age", "age");
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE;

		// when & then
		given().log().all()
			.params(params)
			.when()
			.get("/invalid-model-attribute")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@ParameterizedTest
	@MethodSource("validationTestCases")
	void validationConstraints_예외가_정상적으로_동작한다(ValidationDto dto) {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_NOT_VALID_MESSAGE;

		// when & then
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(dto)
			.when()
			.post("/invalid-request-body")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void queryParameter_타입이_다를_경우_예외가_발생한다() {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE;

		// when & then
		given()
			.param("number", "string")
			.when()
			.get("/query-parameter-request")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void queryParameter가_누락_될_경우_예외가_발생한다() {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.MISSING_PARAMETER_MESSAGE;

		// when & then
		given()
			.when()
			.get("/query-parameter-request")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void pathVariable_타입이_다를_경우_예외가_발생한다() {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.ARGUMENT_TYPE_MISMATCH_MESSAGE;

		// when & then
		given()
			.pathParam("path-variable", "string")
			.when()
			.get("/path-variable-request/{}")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@ParameterizedTest
	@ValueSource(strings = {"{", "{\"}", "{1: \"test\"}"})
	void 중괄호가_잘못되거나_따옴표가_잘못되거나_key값이_숫자라면_예외가_발생한다(String body) {
		// given
		GlobalExceptionMessage message = GlobalExceptionMessage.DATA_NOT_READABLE_MESSAGE;

		// when & then
		given()
			.contentType(MediaType.APPLICATION_JSON)
			.body(body)
			.when()
			.post("/invalid-request-body")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	@Test
	void 지원하지_않는_미디어_타입으로_요청_시_예외가_발생한다() {
		// given
		ValidationDto dto = new ValidationDto(null, null, null);
		GlobalExceptionMessage message = GlobalExceptionMessage.UNSUPPORTED_MEDIA_TYPE_MESSAGE;

		// when & then
		given()
			.body(dto)
			.when()
			.post("/invalid-request-body")
			.then()
			.status(message.getHttpStatus())
			.body("success", equalTo(false))
			.body("message", equalTo(message.getMessage()));
	}

	// 더 많은 제약들이 존재하지만 정상 통과하는 것을 확인
	static Stream<Arguments> validationTestCases() {
		return Stream.of(
			Arguments.of(new ValidationDto("", 1, true), "Empty 검증"),
			Arguments.of(new ValidationDto(" ", 1, true), "Blank 검증"),
			Arguments.of(new ValidationDto("1", 1, true), "Min Size 검증"),
			Arguments.of(new ValidationDto("123456789", 1, true), "Max Size 검증"),
			Arguments.of(new ValidationDto("valid", 0, true), "Min Value 검증"),
			Arguments.of(new ValidationDto("valid", 4, true), "Max Value 검증"),
			Arguments.of(new ValidationDto("valid", -1, true), "Positive 검증")
		);
	}
}
