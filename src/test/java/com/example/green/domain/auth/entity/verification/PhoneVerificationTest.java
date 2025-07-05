package com.example.green.domain.auth.entity.verification;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.green.domain.auth.entity.verification.vo.Attempt;
import com.example.green.domain.auth.entity.verification.vo.PhoneNumber;
import com.example.green.domain.auth.entity.verification.vo.VerificationStatus;
import com.example.green.domain.auth.exception.AuthException;
import com.example.green.domain.auth.exception.PhoneExceptionMessage;

class PhoneVerificationTest {

	private PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");
	private String token = "123456";
	private TokenGenerator tokenGenerator = size -> token;
	private LocalDateTime fixedTime = LocalDateTime.of(2025, 7, 4, 0, 0);

	@Test
	void 전화번호_토큰생성기_시간_정보로_전화_인증_정보_생성시_토큰과_만료_시간을_알_수_있다() {
		// given
		// when
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);

		// then
		assertThat(phoneVerification.getToken()).isEqualTo(token);
		assertThat(phoneVerification.getCreatedAt()).isEqualTo(fixedTime);
		assertThat(phoneVerification.getExpiresAt()).isEqualTo(fixedTime.plusMinutes(10));
	}

	@Test
	void 인증_정보_생성후_토큰_만료_확인시_만료됐으면_상태가_바뀌고_인증실패_예외가_발생한다() {
		// given
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);

		// when & then
		assertThatThrownBy(() -> phoneVerification.verifyExpiration(fixedTime.plusMinutes(11)))
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PhoneExceptionMessage.VERIFICATION_EXPIRED);
	}

	@Test
	void 인증_정보_생성후_토큰_만료_확인시_만료되지_않았다면_아무_일도_발생하지_않는다() {
		// given
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);

		// when & then
		assertThat(phoneVerification.getExpiresAt()).isEqualTo(fixedTime.plusMinutes(10));
	}

	@Test
	void 인증_정보_생성후_토큰_검증에_성공하면_시도횟수가_증가하고_검증된_상태로_밥뀐다() {
		// given
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);
		Attempt increasedAttempt = phoneVerification.getAttempt().increaseCount();

		// when
		phoneVerification.verifyToken(token);

		// then
		assertThat(phoneVerification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
		assertThat(phoneVerification.getAttempt()).isEqualTo(increasedAttempt);
	}

	@Test
	void 인증정보_생성후_토큰_검증에_실패해도_시도_횟수는_증가하고_예외를_반환하고_상태_변화는_없다() {
		// given
		String invalidToken = "000000";
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);
		Attempt increasedAttempt = phoneVerification.getAttempt().increaseCount();

		// when & then
		assertThatThrownBy(() -> phoneVerification.verifyToken(invalidToken))
			.isInstanceOf(AuthException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", PhoneExceptionMessage.TOKEN_MISMATCH);
		assertThat(phoneVerification.getStatus()).isEqualTo(VerificationStatus.PENDING);
		assertThat(phoneVerification.getAttempt()).isEqualTo(increasedAttempt);
	}

	@Test
	void 인증정보는_재인증_상태가_될_수_있다() {
		// given
		PhoneVerification phoneVerification = PhoneVerification.of(phoneNumber, tokenGenerator, fixedTime);

		// when
		phoneVerification.markAsReissue();

		// then
		assertThat(phoneVerification.getStatus()).isEqualTo(VerificationStatus.REISSUED);
	}
}