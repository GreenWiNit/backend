package com.example.green.domain.challenge.entity.challenge.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class ChallengeInfoTest {

	@Test
	void 챌린지_정보는_이름_포인트로_구성된다() {
		// given
		String name = "challengeName";
		Integer point = 1000;

		// when
		ChallengeInfo info = ChallengeInfo.of(name, point);

		// then
		assertThat(info.getName()).isEqualTo(name);
		assertThat(info.getPoint()).isEqualTo(point);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 챌린지_이름_정보는_비어_있을_수_없다(String emptyName) {
		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of(emptyName, 1000))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NAME_BLANK);
	}

	@Test
	void 챌린지_이름은_90자_제한이_있다() {
		// given
		String challengeName = "1".repeat(91);

		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of(challengeName, 1000))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NAME_LENGTH_EXCEEDED);
	}

	@Test
	void 챌린지_포인트_정보는_필수다() {
		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("challengeName", null))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_POINT_BLANK);
	}

	@Test
	void 포인트는_0원_이상이다() {
		// given
		Integer negativePoint = -1;

		// when & then
		assertThatThrownBy(() -> ChallengeInfo.of("challengeName", negativePoint))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.POINT_LESS_THAN_ZERO);
	}
}