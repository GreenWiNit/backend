package com.example.integration.challenge.query;

import static org.assertj.core.api.Assertions.*;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.global.api.page.CursorTemplate;

public class ChallengeQueryTestHelper {

	public static void 참여_챌린지_검증(CursorTemplate<Long, ChallengeDto> result, ChallengeType type) {
		// 타입에 따른 아이디 순서
		챌린지_별_ID_검증(result, type);
		// 커서랑 챌린지 ID랑 다름
		assertThat(result.nextCursor()).isNotEqualTo(result.content().getLast().getId());
	}

	public static void 챌린지_조회_검증(CursorTemplate<Long, ChallengeDto> result, ChallengeType type) {
		// 타입에 따른 아이디 순서
		챌린지_별_ID_검증(result, type);
		// 챌린지 조회는 ID가 커서
		assertThat(result.nextCursor()).isEqualTo(result.content().getLast().getId());
	}

	private static void 챌린지_별_ID_검증(CursorTemplate<Long, ChallengeDto> result, ChallengeType type) {
		assertThat(result.content()).extracting(ChallengeDto::getId)
			.allMatch(id -> {
				if (type == ChallengeType.PERSONAL) {
					return id % 2 == 0;
				}
				return id % 2 == 1;
			});
	}
}
