package com.example.green.domain.pointshop.entity.pointproduct.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class BasicInfoTest {

	String code = "PRD-AA-001";
	String name = "name";
	String description = "description";

	@Test
	void 상품_기본_정보를_생성한다() {
		// given
		// when
		BasicInfo basicInfo = new BasicInfo(code, name, description);

		// then
		assertThat(basicInfo.getCode()).isEqualTo(code);
		assertThat(basicInfo.getName()).isEqualTo(name);
		assertThat(basicInfo.getDescription()).isEqualTo(description);
	}

	@ParameterizedTest
	@MethodSource("invalidProductCodeTestCases")
	void 상품_기본_정보인_상품_코드는_필수_값으로_코드_형식에_맞지_않으면_생성할_수_없다(String invalidCode, String 주석) {
		// given
		// when & then
		assertThatThrownBy(() -> new BasicInfo(invalidCode, name, description))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 상품_기본_정보인_상품명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> new BasicInfo(code, null, description))
			.isInstanceOf(BusinessException.class);
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 16})
	void 상품_기본_정보인_상품명은_2글자에서_15글자_사이로_구성되어있지_않다면_생성할_수_없다(int repeatCount) {
		// given
		String invalidName = "a".repeat(repeatCount);
		// when & then
		assertThatThrownBy(() -> new BasicInfo(code, invalidName, description))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_NAME);
	}

	@Test
	void 상품_기본_정보인_상품_설명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> new BasicInfo(code, name, null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 상품_기본_정보인_상품_설명은_100글자_이내로_구성되어있지_않다면_생성할_수_없다() {
		// given
		String invalidDescription = "a".repeat(101);
		// when & then
		assertThatThrownBy(() -> new BasicInfo(code, name, invalidDescription))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_DESCRIPTION);
	}

	static Stream<Arguments> invalidProductCodeTestCases() {
		return Stream.of(
			Arguments.of("PRD-AA-12", "길이 부족"),
			Arguments.of("PRD-AA-1234", "길이 초과"),
			Arguments.of("PRD-ab-123", "상품명 부분 소문자"),
			Arguments.of("PRD-A1-123", "상품명 부분 숫자"),
			Arguments.of("PRD-AA-12A", "숫자 부분에 문자 포함"),
			Arguments.of("PRD_AA+123", "하이픈 아님"),
			Arguments.of("pRD-AA-123", "PRD 소문자"),
			Arguments.of(null, "null 값")
		);
	}
}