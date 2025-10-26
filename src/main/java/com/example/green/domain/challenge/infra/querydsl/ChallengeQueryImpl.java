package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.infra.querydsl.executor.ChallengeClientQueryExecutor;
import com.example.green.domain.challenge.infra.querydsl.predicates.ChallengePredicates;
import com.example.green.domain.challenge.repository.query.ChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeQueryImpl implements ChallengeQuery {

	private final ChallengeClientQueryExecutor executor;

	@Override
	public CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(
		Long memberId, Long cursor, int size, ChallengeType type
	) {
		BooleanExpression condition = ChallengePredicates.myParticipationCondition(memberId, cursor, type);
		List<ChallengeDto> participated = executor.executeParticipationQuery(condition, size);
		return CursorTemplate.from(participated, size, ChallengeDto::getCursor);
	}

	@Override
	public CursorTemplate<Long, ChallengeDto> findChallengesByCursor(Long cursor, int size, ChallengeType type) {
		BooleanExpression condition = ChallengePredicates.activeChallengeCondition(cursor, type);
		List<ChallengeDto> challenges = executor.executeChallengesQuery(condition, size);
		return CursorTemplate.from(challenges, size, ChallengeDto::getId);
	}

	@Override
	public ChallengeDetailDtoV2 findChallenge(Long challengeId, Long memberId) {
		BooleanExpression condition = ChallengePredicates.memberParticipationExists(challengeId, memberId);
		ChallengeDetailDtoV2 result = executor.executeChallengeDetailQuery(condition, challengeId);
		return Optional.ofNullable(result)
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}
}
