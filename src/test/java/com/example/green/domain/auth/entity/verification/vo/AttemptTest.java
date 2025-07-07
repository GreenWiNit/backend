package com.example.green.domain.auth.entity.verification.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;

class AttemptTest {

	@Test
	void 기본_시도횟수는_0회_이다() {
		// when
		Attempt init = Attempt.init();

		// then
		assertThat(init.getAttempts()).isZero();
	}

	@Test
	void 시도횟수가_5회를_초과하면_예외가_발생한다() {
		// given
		Attempt attempt = Attempt.init();
		for (int i = 0; i < 5; i++) {
			attempt = attempt.increaseCount();
		}

		// when & then
		assertThatThrownBy(attempt::increaseCount)
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PhoneExceptionMessage.OVER_MAX_TRY);
	}
}