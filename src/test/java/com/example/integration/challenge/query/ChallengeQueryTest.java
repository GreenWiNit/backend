package com.example.integration.challenge.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
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
		dataSource.챌린지_50개_생성();
	}

	@ParameterizedTest
	@EnumSource(ChallengeType.class)
	void 참여한_챌린지_조회(ChallengeType type) {
		// given: ID 내림차순으로 챌린지 참여
		dataSource.챌린지_참여_역순();

		// when : 31번 아이디부터 개인챌린지 조회
		CursorTemplate<Long, ChallengeDto> result = challengeQuery.findMyParticipationByCursor(1L, 999L, 10, type);

		// then : 30번부터 짝수번째만 개인, 홀수번째 팀
		ChallengeQueryTestHelper.참여_챌린지_검증(result, type);
	}

}
