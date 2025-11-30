package com.example.integration.challenge.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.exception.ChallengeExceptionMessage;
import com.example.green.domain.challenge.repository.query.ChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.example.integration.challenge.ChallengeTestDataSource;
import com.example.integration.common.BaseIntegrationTest;

@Transactional
public class ChallengeQueryTest extends BaseIntegrationTest {

	@Autowired
	private ChallengeTestDataSource dataSource;

	@Autowired
	private ChallengeQuery challengeQuery;

	@BeforeEach
	void setUp() {
		dataSource.init();
		dataSource.챌린지_50개_생성();
	}

	@ParameterizedTest
	@EnumSource(ChallengeType.class)
	void 참여한_챌린지_조회(ChallengeType type) {
		// given: ID 내림차순으로 챌린지 참여
		dataSource.챌린지_참여_역순();

		// when : 999번 아이디부터 개인챌린지 조회
		CursorTemplate<Long, ChallengeDto> result = challengeQuery.findMyParticipationByCursor(1L, 999L, 20, type);

		// then : 짝수번째만 개인, 홀수번째 팀
		ChallengeQueryTestHelper.참여_챌린지_검증(result, type);
	}

	@ParameterizedTest
	@EnumSource(ChallengeType.class)
	void 챌린지_목록_조회(ChallengeType type) {
		// when
		CursorTemplate<Long, ChallengeDto> result = challengeQuery.findChallengesByCursor(999L, 20, type);

		// then
		ChallengeQueryTestHelper.챌린지_조회_검증(result, type);
	}

	@Test
	void 미참여_챌린지_상세_조회() {
		// when
		ChallengeDetailDtoV2 result = challengeQuery.findChallenge(1L, 1L);

		// then
		assertThat(result.participating()).isFalse();
	}

	@Test
	void 참여_챌린지_상세_조회() {
		// given
		dataSource.챌린지_하나_참여(1L, 1L);

		// when
		ChallengeDetailDtoV2 result = challengeQuery.findChallenge(1L, 1L);

		// then
		assertThat(result.participating()).isTrue();
	}

	@Test
	void 없는_챌린지_상세_조회() {
		// when & then
		assertThatThrownBy(() -> challengeQuery.findChallenge(51L, 1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NOT_FOUND);
	}

	@Test
	void 미공개_챌린지_상세_조회() {
		dataSource.챌린지_미공개(1L);

		// when & then
		assertThatThrownBy(() -> challengeQuery.findChallenge(1L, 1L))
			.isInstanceOf(ChallengeException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", ChallengeExceptionMessage.CHALLENGE_NOT_FOUND);
	}
}
