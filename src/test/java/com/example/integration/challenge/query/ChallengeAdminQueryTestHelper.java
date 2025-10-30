package com.example.integration.challenge.query;

import java.util.stream.Stream;

import org.junit.jupiter.params.provider.Arguments;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;

public class ChallengeAdminQueryTestHelper {

	public static Stream<Arguments> detailQueryDataSet() {
		// given: 홀수번째 ID = 팀 챌린지, 짝수번째 ID = 개인 챌린지
		return Stream.of(
			Arguments.of(ChallengeType.TEAM, 1L),
			Arguments.of(ChallengeType.PERSONAL, 2L)
		);
	}
}
