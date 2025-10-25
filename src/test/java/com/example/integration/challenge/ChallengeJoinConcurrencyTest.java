package com.example.integration.challenge;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.service.TeamChallengeService;
import com.example.integration.common.BaseIntegrationTest;
import com.example.integration.common.concurrency.ConcurrencyTestResult;
import com.example.integration.common.concurrency.ConcurrencyTestTemplate;

class ChallengeJoinConcurrencyTest extends BaseIntegrationTest {

	@Autowired
	private TeamChallengeService teamChallengeService;

	@Autowired
	private TeamChallengeRepository teamChallengeRepository;

	private TeamChallenge teamChallenge;

	@BeforeEach
	void setUp() {
		teamChallengeRepository.deleteAllInBatch();

		// 팀 챌린지 생성 (참가자 수 제한 없음)
		teamChallenge = TeamChallenge.create(
			"CHL-001",
			"환경 정화 챌린지",
			"thumbnail.jpg",
			"환경 정화 활동을 함께 해요!",
			BigDecimal.valueOf(100),
			LocalDate.now(),
			LocalDate.now().plusDays(7),
			ChallengeDisplay.VISIBLE
		);
		teamChallenge = teamChallengeRepository.saveAndFlush(teamChallenge);
	}

	@Test
	void 동시에_3명이_챌린지에_참여하면_최종_참가자_수는_3명이다() throws InterruptedException {
		// given
		AtomicLong memberIdGenerator = new AtomicLong(1);

		// when & then
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(3)
			.timeout(5)
			.execute(() -> {
				try {
					teamChallengeService.join(teamChallenge.getId(), memberIdGenerator.getAndIncrement());
					return true;
				} catch (Exception e) {
					// 이미 참여한 경우나 다른 예외가 발생할 수 있음
					return false;
				}
			});

		TeamChallenge finalChallenge = teamChallengeRepository.findById(teamChallenge.getId()).orElseThrow();
		assertThat(result.allSucceeded()).isTrue();
		assertThat(finalChallenge.getParticipantCount()).isEqualTo(3);
	}
}
