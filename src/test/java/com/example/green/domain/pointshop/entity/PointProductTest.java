package com.example.green.domain.pointshop.entity;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.domain.pointshop.entity.vo.PointProductDisplay;
import com.example.green.domain.pointshop.entity.vo.PointProductStatus;
import com.example.green.global.error.exception.BusinessException;

class PointProductTest {

	String code = "PRD-AA-001";
	String name = "name";
	String description = "description";
	String thumbnail = "http://example.com/image.jpg";
	int point = 1000;
	int stock = 100;

	@Test
	void 새로운_포인트_상품을_생성하면_판매상태이고_전시상태이다() {
		// given
		// when
		PointProduct pointProduct = PointProduct.create(code, name, description, thumbnail, point, stock);

		// then
		assertThat(pointProduct).isNotNull();
		assertThat(pointProduct.getStatus()).isEqualTo(PointProductStatus.IN_STOCK);
		assertThat(pointProduct.getDisplay()).isEqualTo(PointProductDisplay.DISPLAY);
	}

	@ParameterizedTest
	@MethodSource("invalidProductCodeTestCases")
	void 상품_코드는_필수_값으로_코드_형식에_맞지_않으면_생성할_수_없다(String invalidCode, String 주석) {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(invalidCode, name, description, thumbnail, point, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_CODE);
	}

	@ParameterizedTest
	@ValueSource(ints = -1)
	@NullSource
	void 상품_생성시_포인트는_필수값으로_0원_이상이_아니면_생성할_수_없다(Integer invalidPoint) {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, name, description, thumbnail, invalidPoint, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_POINT);
	}

	@ParameterizedTest
	@ValueSource(ints = {-1, 0})
	@NullSource
	void 상품_생성시_상품_재고는_필수값으로_1개_이상이_아니라면_생성할_수_없다(Integer invalidStock) {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, name, description, thumbnail, point, invalidStock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_STOCK);
	}

	@Test
	void 상품_생성시_상품명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, null, description, thumbnail, point, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_NAME);
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 16})
	void 상품_생성시_상품명은_2글자에서_15글자_사이로_구성되어있지_않다면_생성할_수_없다(int repeatCount) {
		// given
		String invalidName = "a".repeat(repeatCount);
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, invalidName, description, thumbnail, point, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_NAME);
	}

	@ParameterizedTest
	@NullSource
	@ValueSource(strings = "NO_URI")
	void 상품_생성시_썸네일_이미지는_필수_정보로_URI_형식이_아니라면_생성할_수_없다(String invalidThumbnail) {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, name, description, invalidThumbnail, point, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_THUMBNAIL);
	}

	@Test
	void 상품_생성시_상품_설명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, name, null, thumbnail, point, stock))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_DESCRIPTION);
	}

	@Test
	void 상품_생성시_상품_설명은_100글자_이내로_구성되어있지_않다면_생성할_수_없다() {
		// given
		String invalidDescription = "a".repeat(101);
		// when & then
		assertThatThrownBy(() -> PointProduct.create(code, name, invalidDescription, thumbnail, point, stock))
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