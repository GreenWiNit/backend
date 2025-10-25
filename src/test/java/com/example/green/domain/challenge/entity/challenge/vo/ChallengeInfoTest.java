package com.example.green.domain.challenge.entity.challenge.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class ChallengeInfoTest {

	@Test
	void 챌린지_정보는_코드_이름_포인트로_구성된다() {
		// given
		String code = "code";
		String name = "challengeName";
		Integer point = 1000;

		// when
		ChallengeInfo info = ChallengeInfo.of(code, name, point);

		// then
		assertThat(info.getCode()).isEqualTo(code);
		assertThat(info.getName()).isEqualTo(name);
		assertThat(info.getPoint()).isEqualTo(point);
	}

	@Test
	void 챌린지_코드_정보는_필수다() {
		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of(null, "challengeName", 1000))
			.isInstanceOf(NullPointerException.class)
			.hasMessageContaining(ChallengeExceptionMessage.CHALLENGE_CODE_NON_NULL);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 챌린지_이름_정보는_비어_있을_수_없다(String emptyName) {
		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("code", emptyName, 1000))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NAME_EMPTY);
	}

	@Test
	void 챌린지_이름은_90자_제한이_있다() {
		// given
		String challengeName = "1".repeat(91);

		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("code", challengeName, 1000))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NAME_LENGTH_EXCEEDED);
	}

	@Test
	void 챌린지_포인트_정보는_필수다() {
		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("code", "challengeName", null))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_POINT_EMPTY);
	}

	@Test
	void 포인트는_0원_이상이다() {
		// given
		Integer negativePoint = -1;

		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("code", "challengeName", negativePoint))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.POINT_LESS_THAN_ZERO);
	}
}