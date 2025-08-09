package com.example.green.domain.challenge.repository.query;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;

public interface TeamChallengeQuery {

	CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(Long memberId, Long cursor, int size);

	CursorTemplate<Long, ChallengeListResponseDto> findTeamChallengesByCursor(
		Long cursor, int size, ChallengeStatus challengeStatus, LocalDateTime now);

	ChallengeDetailDto findTeamChallenge(Long challengeId, Long memberId);
}
