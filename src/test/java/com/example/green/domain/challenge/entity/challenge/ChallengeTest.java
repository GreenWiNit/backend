package com.example.green.domain.challenge.entity.challenge;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;

class ChallengeTest {

	ChallengeInfo challengeInfo = mock(ChallengeInfo.class);
	ChallengeContent challengeContent = mock(ChallengeContent.class);

	@Test
	void 챌린지_정보와_콘텐츠로_개인_챌린지를_생성한다() {
		// when
		Challenge result = Challenge.ofPersonal(challengeInfo, challengeContent);

		// then
		assertThat(result.getInfo()).isEqualTo(challengeInfo);
		assertThat(result.getContent()).isEqualTo(challengeContent);
		assertThat(result.getType()).isEqualTo(ChallengeType.PERSONAL);
		assertThat(result.getParticipantCount()).isEqualTo(0);
		assertThat(result.getDisplay()).isEqualTo(ChallengeDisplay.VISIBLE);
	}

	@Test
	void 팀_챌린지도_생성_할_수_있다() {
		// when
		Challenge result = Challenge.ofTeam(challengeInfo, challengeContent);

		// then
		assertThat(result.getType()).isEqualTo(ChallengeType.TEAM);
	}

	@Test
	void 챌린지를_미전시한다() {
		// given
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);

		// when
		challenge.hide();

		// then
		assertThat(challenge.getDisplay()).isEqualTo(ChallengeDisplay.HIDDEN);
	}

	@Test
	void 챌린지_정보를_수정한다() {
		// given
		ChallengeInfo newInfo = mock(ChallengeInfo.class);
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);

		// when
		challenge.updateInfo(newInfo);

		// then
		assertThat(challenge.getInfo()).isEqualTo(newInfo);
	}

	@Test
	void 챌린지_콘텐츠를_수정한다() {
		ChallengeContent newContent = mock(ChallengeContent.class);
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);

		// when
		challenge.updateContent(newContent);

		// then
		assertThat(challenge.getContent()).isEqualTo(newContent);
	}

	@Test
	void 챌린지에_참여하면_인원이_증가한다() {
		// given
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);
		int before = challenge.getParticipantCount();

		// when
		challenge.participate(1L);

		// then
		assertThat(challenge.getParticipantCount()).isEqualTo(before + 1);
	}

	@Test
	void 이미_참여한_경우_예외가_발생한다() {
		// given
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);
		challenge.participate(1L);

		// when & then
		assertThatThrownBy(() -> challenge.participate(1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.ALREADY_PARTICIPATING);
	}

	@Test
	void 미전시_챌린지는_참여할_수_없다() {
		// given
		Challenge challenge = Challenge.ofPersonal(challengeInfo, challengeContent);
		challenge.hide();

		// when & then
		assertThatThrownBy(() -> challenge.participate(1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.INACTIVE_CHALLENGE);
	}
}