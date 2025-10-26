package com.example.green.domain.challenge.repository.query;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.global.api.page.CursorTemplate;

public interface ChallengeQuery {

	CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(
		Long memberId, Long cursor, int size, ChallengeType type);

	CursorTemplate<Long, ChallengeDto> findChallengesByCursor(Long cursor, int size, ChallengeType type);

	ChallengeDetailDtoV2 findChallenge(Long challengeId, Long memberId);
}
