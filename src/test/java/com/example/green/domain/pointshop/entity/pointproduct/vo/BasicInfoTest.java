package com.example.green.domain.pointshop.entity.pointproduct.vo;

import static com.example.green.domain.pointshop.exception.PointProductExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.global.error.exception.BusinessException;

class BasicInfoTest {

	String name = "name";
	String description = "description";

	@Test
	void 상품_기본_정보를_생성한다() {
		// given
		// when
		BasicInfo basicInfo = new BasicInfo(name, description);

		// then
		assertThat(basicInfo.getName()).isEqualTo(name);
		assertThat(basicInfo.getDescription()).isEqualTo(description);
	}

	@Test
	void 상품_기본_정보인_상품명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> new BasicInfo(null, description))
			.isInstanceOf(BusinessException.class);
	}

	@ParameterizedTest
	@ValueSource(ints = {1, 16})
	void 상품_기본_정보인_상품명은_2글자에서_15글자_사이로_구성되어있지_않다면_생성할_수_없다(int repeatCount) {
		// given
		String invalidName = "a".repeat(repeatCount);
		// when & then
		assertThatThrownBy(() -> new BasicInfo(invalidName, description))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_NAME);
	}

	@Test
	void 상품_기본_정보인_상품_설명은_필수값으로_없는_경우_생성할_수_없다() {
		// given
		// when & then
		assertThatThrownBy(() -> new BasicInfo(name, null))
			.isInstanceOf(BusinessException.class);
	}

	@Test
	void 상품_기본_정보인_상품_설명은_100글자_이내로_구성되어있지_않다면_생성할_수_없다() {
		// given
		String invalidDescription = "a".repeat(101);
		// when & then
		assertThatThrownBy(() -> new BasicInfo(name, invalidDescription))
			.isInstanceOf(BusinessException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PRODUCT_DESCRIPTION);
	}
}