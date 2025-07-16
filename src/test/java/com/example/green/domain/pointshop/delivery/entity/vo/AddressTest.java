package com.example.green.domain.pointshop.delivery.entity.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.domain.pointshop.delivery.exception.DeliveryAddressException;
import com.example.green.domain.pointshop.delivery.exception.DeliveryAddressExceptionMessage;

class AddressTest {

	@ParameterizedTest
	@ValueSource(strings = {
		"a1111",    // 문자 포함
		"1111",    // 4자리
		"111111"        // 6자리
	})
	void 우편번호는_5자리_숫자로_구성된다(String invalidZipCode) {
		// given
		// when & then
		assertThatThrownBy(() -> Address.of("road", "detail", invalidZipCode))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", DeliveryAddressExceptionMessage.INVALID_ZIP_CODE);
	}
}