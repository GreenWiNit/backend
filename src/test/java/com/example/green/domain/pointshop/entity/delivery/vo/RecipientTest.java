package com.example.green.domain.pointshop.entity.delivery.vo;

import static com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressExceptionMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.domain.pointshop.exception.deliveryaddress.DeliveryAddressException;

class RecipientTest {

	@ParameterizedTest
	@ValueSource(strings = {
		"011-1234-5678",   // 010이 아닌 번호
		"010-12345-678",   // 중간 번호가 5자리
		"010-123-5678",    // 중간 번호가 3자리
		"010-1234-567",    // 마지막 번호가 3자리
		"010-1234-56789",  // 마지막 번호가 5자리
		"010-abcd-5678",   // 문자 포함
		"010 1234 5678",   // 공백 포함
		"01012345678",     // 하이픈 없음
	})
	void 수령자_전화번호는_유효한_형식이어야_한다(String invalidPhoneNumber) {
		// given
		// when & then
		assertThatThrownBy(() -> Recipient.of(1L, "나", invalidPhoneNumber))
			.isInstanceOf(DeliveryAddressException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", INVALID_PHONE_NUMBER);
	}

}