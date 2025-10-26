package com.example.green.domain.challenge.entity.challenge;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class ChallengeTest {

	String challengeCode = "code";
	ChallengeInfo challengeInfo = mock(ChallengeInfo.class);
	ChallengeContent challengeContent = mock(ChallengeContent.class);

	@Test
	void 개인_챌린지를_생성_할_수_있다() {
		// when
		Challenge result = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.PERSONAL);

		// then
		assertThat(result.getType()).isEqualTo(ChallengeType.PERSONAL);
	}

	@Test
	void 팀_챌린지도_생성_할_수_있다() {
		// when
		Challenge result = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);

		// then
		assertThat(result.getType()).isEqualTo(ChallengeType.TEAM);
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 챌린지_코드가_없으면_생성할_수_없다(String invalidCode) {
		// when & then
		assertThatThrownBy(() -> Challenge.of(invalidCode, challengeInfo, challengeContent, ChallengeType.TEAM))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_CODE_BLANK);
	}

	@Test
	void 챌린지를_미전시한다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);

		// when
		challenge.hide();

		// then
		assertThat(challenge.getDisplay()).isEqualTo(ChallengeDisplay.HIDDEN);
	}

	@Test
	void 미전시된_챌린지를_전시한다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);
		challenge.hide();

		// when
		challenge.show();

		// then
		assertThat(challenge.getDisplay()).isEqualTo(ChallengeDisplay.VISIBLE);
	}

	@Test
	void 챌린지_정보를_수정한다() {
		// given
		ChallengeInfo newInfo = mock(ChallengeInfo.class);
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);

		// when
		challenge.updateInfo(newInfo);

		// then
		assertThat(challenge.getInfo()).isEqualTo(newInfo);
	}

	@Test
	void 챌린지_콘텐츠를_수정한다() {
		ChallengeContent newContent = mock(ChallengeContent.class);
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);

		// when
		challenge.updateContent(newContent);

		// then
		assertThat(challenge.getContent()).isEqualTo(newContent);
	}

	@Test
	void 챌린지에_참여하면_인원이_증가한다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);
		int before = challenge.getParticipantCount();

		// when
		challenge.participate(1L);

		// then
		assertThat(challenge.getParticipantCount()).isEqualTo(before + 1);
	}

	@Test
	void 이미_참여한_경우_예외가_발생한다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);
		challenge.participate(1L);

		// when & then
		assertThatThrownBy(() -> challenge.participate(1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.ALREADY_PARTICIPATING);
	}

	@Test
	void 미전시_챌린지는_참여할_수_없다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);
		challenge.hide();

		// when & then
		assertThatThrownBy(() -> challenge.participate(1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INACTIVE_CHALLENGE);
	}

	@Test
	void 챌린지_이미지_조회시_콘텐츠_이미지_정보를_가져온다() {
		// given
		Challenge challenge = Challenge.of(challengeCode, challengeInfo, challengeContent, ChallengeType.TEAM);

		// when
		String result = challenge.getImageUrl();

		// then
		assertThat(result).isEqualTo(challengeContent.getImageUrl());
	}
}