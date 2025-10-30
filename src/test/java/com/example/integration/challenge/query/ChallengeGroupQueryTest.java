package com.example.integration.challenge.query;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.green.domain.challenge.entity.group.ChallengeGroup;
import com.example.green.domain.challenge.entity.group.GroupAddress;
import com.example.green.domain.challenge.entity.group.GroupBasicInfo;
import com.example.green.domain.challenge.entity.group.GroupPeriod;
import com.example.green.domain.challenge.repository.ChallengeGroupRepository;
import com.example.green.domain.challenge.repository.query.ChallengeGroupQuery;
import com.example.integration.common.BaseIntegrationTest;

public class ChallengeGroupQueryTest extends BaseIntegrationTest {

	@Autowired
	private ChallengeGroupRepository challengeGroupRepository;

	@Autowired
	private ChallengeGroupQuery challengeGroupQuery;

	@BeforeEach
	void setUp() {
		challengeGroupRepository.deleteAllInBatch();
		Long id = 0L;
		List<ChallengeGroup> challengeGroups = new ArrayList<>();
		for (int i = 1; i <= 50; i++) {
			if ((i - 1) % 10 == 0) {
				id++;
			}
			GroupBasicInfo basicInfo = GroupBasicInfo.of("챌린지 팀", "설명", "https://url.test/a.png");
			GroupAddress address = GroupAddress.of("서울", "강남구", "123-456");
			GroupPeriod period = GroupPeriod.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1 + i));

			ChallengeGroup group = ChallengeGroup.create("teamCode", id, 1L, basicInfo, address, 10, period);
			challengeGroups.add(group);
		}
		challengeGroupRepository.saveAllAndFlush(challengeGroups);
	}

	@Test
	void 챌린지_id별_팀_수_조회() {
		// given
		List<Long> challengeIds = List.of(1L, 2L, 3L, 4L, 5L);

		// when
		Map<Long, Long> result = challengeGroupQuery.countByChallengeIds(challengeIds);

		// then: 각 아이디 별로 10개씩 팀이 있음.
		assertThat(result.values()).allMatch(count -> count == 10L);
	}
}
