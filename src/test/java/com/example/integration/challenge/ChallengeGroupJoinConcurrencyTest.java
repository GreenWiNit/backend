package com.example.integration.challenge;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.entity.group.GroupAddress;
import com.example.green.domain.challenge.entity.group.GroupBasicInfo;
import com.example.green.domain.challenge.entity.group.GroupPeriod;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.service.ChallengeGroupService;
import com.example.integration.common.BaseIntegrationTest;
import com.example.integration.common.concurrency.ConcurrencyTestResult;
import com.example.integration.common.concurrency.ConcurrencyTestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ChallengeGroupJoinConcurrencyTest extends BaseIntegrationTest {

	@Autowired
	private ChallengeGroupService challengeGroupService;

	@Autowired
	private ChallengeGroupRepository challengeGroupRepository;

	private ChallengeGroup challengeGroup;

	@BeforeEach
	void setUp() {
		challengeGroupRepository.deleteAllInBatch();

		// 챌린지 그룹 생성 (최대 100명 참여 가능)
		challengeGroup = ChallengeGroup.create(
			"GRP-001",
			1L,
			1L, // 리더 ID
			GroupBasicInfo.of("그룹1", "그룹 설명", "오픈채팅방 URL"),
			GroupAddress.of("서울시", "강남구", "123-456"),
			10, // 최대 참가자 수
			GroupPeriod.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(7))
		);
		challengeGroup = challengeGroupRepository.saveAndFlush(challengeGroup);
		log.info("init: challenge groud Id = {}", challengeGroup.getId());
	}

	@Test
	void 동시에_3명의_사용자가_그룹에_참여하면_최종_참가자_수는_4명이_되어야_한다() throws InterruptedException {
		// given
		AtomicLong memberIdGenerator = new AtomicLong(2);

		// when & then - 서비스를 통한 통합 테스트
		ConcurrencyTestResult result = ConcurrencyTestTemplate.build()
			.threadCount(3)  // ← 단일 스레드로 먼저 테스트
			.timeout(5)
			.execute(() -> {
				try {
					Long memberId = memberIdGenerator.getAndIncrement();
					challengeGroupService.join(challengeGroup.getId(), memberId);
					return true;
				} catch (Exception e) {
					log.warn("서비스 호출 실패: error={}", e.getMessage(), e);
					return false;
				}
			});

		// 통합 테스트 검증
		ChallengeGroup group = challengeGroupRepository.findById(challengeGroup.getId()).orElseThrow();
		assertThat(result.allSucceeded()).isTrue();
		assertThat(group.getCapacity().getCurrentParticipants()).isEqualTo(4); // 방장 1명 + 참여자 3명
	}
}
