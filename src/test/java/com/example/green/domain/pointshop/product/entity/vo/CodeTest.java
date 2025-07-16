package com.example.green.domain.pointshop.product.entity.vo;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.example.green.global.error.exception.BusinessException;

class CodeTest {

	@Test
	void 상품_코드가_소문자여도_대문자로_변환한다() {
		// given
		String lowerCode = "prd-aa-001";

		// when
		Code code = new Code(lowerCode);

		// then
		assertThat(code.getCode()).isEqualTo(lowerCode.toUpperCase());
	}

	@ParameterizedTest
	@MethodSource("invalidProductCodeTestCases")
	void 상품_코드는_필수_값으로_코드_형식에_맞지_않으면_생성할_수_없다(String invalidCode, String 주석) {
		// given & when & then
		assertThatThrownBy(() -> new Code(invalidCode)).isInstanceOf(BusinessException.class);
	}

	static Stream<Arguments> invalidProductCodeTestCases() {
		return Stream.of(
			Arguments.of("PRD-AA-12", "길이 부족"),
			Arguments.of("PRD-AA-1234", "길이 초과"),
			Arguments.of("PRD-A1-123", "상품명 부분 숫자"),
			Arguments.of("PRD-AA-12A", "숫자 부분에 문자 포함"),
			Arguments.of("PRD_AA+123", "하이픈 아님"),
			Arguments.of(null, "null 값")
		);
	}
}