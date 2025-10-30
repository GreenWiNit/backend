package com.example.integration.challenge.query;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.repository.query.ChallengeAdminQuery;
import com.example.green.global.api.page.PageTemplate;
import com.example.integration.challenge.ChallengeTestDataSource;
import com.example.integration.common.BaseIntegrationTest;

@Transactional
public class ChallengeAdminQueryTest extends BaseIntegrationTest {

	@Autowired
	private ChallengeTestDataSource dataSource;

	@Autowired
	private ChallengeAdminQuery challengeAdminQuery;

	@BeforeEach
	void setUp() {
		dataSource.챌린지_50개_생성();
	}

	@ParameterizedTest
	@MethodSource("com.example.integration.challenge.query.ChallengeAdminQueryTestHelper#detailQueryDataSet")
	void 챌린지_상세_조회(ChallengeType type, Long id) {
		// when
		AdminChallengeDetailDto result = challengeAdminQuery.getChallengeDetail(id);

		// then
		assertThat(result.challengeType()).isEqualTo(type);
	}

	@ParameterizedTest
	@EnumSource(ChallengeType.class)
	void 챌린지_페이지_조회(ChallengeType type) {
		// when
		PageTemplate<AdminChallengesDto> result = challengeAdminQuery.findChallengePage(1, 10, type);

		// then:
		assertThat(result.totalElements()).isEqualTo(25L);
	}

	@Test
	void 챌린지_별_참여자_조회() {
		// given
		dataSource.챌린지_하나_참여(1L, 1L);

		// when
		PageTemplate<AdminPersonalParticipationDto> result = challengeAdminQuery.findParticipantByChallenge(1L, 1, 10);

		// then
		assertThat(result.content()).hasSize(1);
	}
}
