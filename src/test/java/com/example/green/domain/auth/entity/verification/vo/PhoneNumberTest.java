package com.example.green.domain.auth.entity.verification.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;

class PhoneNumberTest {

	@ParameterizedTest
	@CsvSource({
		"010-1234-56788",    // 길이 초과
		"010-123-5678",        // 길이 미만
		"0101234-5678",        // 하이픈 없음
		"011-1234-5678",        // 최신 전화 형식 아님
		"010-a234-5678"        // 문자 포함
	})
	void 유효한_전화번호_형식이_아니라면_예외가_발생한다(String number) {
		// given
		// when & then
		assertThatThrownBy(() -> PhoneNumber.of(number))
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PhoneExceptionMessage.INVALID_PHONE_NUMBER);
	}

}