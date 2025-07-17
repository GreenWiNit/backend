package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeamChallengeRepositoryImpl implements TeamChallengeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeListResponseDto> findTeamChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		List<TeamChallenge> challenges = queryFactory
			.selectFrom(teamChallenge)
			.where(
				cursorCondition(cursor),
				teamChallenge.challengeStatus.eq(status),
				teamChallenge.beginDateTime.loe(now),
				teamChallenge.endDateTime.goe(now)
			)
			.orderBy(teamChallenge.id.desc())
			.limit(size)
			.fetch();

		if (challenges.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<ChallengeListResponseDto> dtos = challenges.stream()
			.map(this::toChallengeListDto)
			.toList();

		Long nextCursor = challenges.get(challenges.size() - 1).getId();
		boolean hasNext = hasNextTeamChallenge(nextCursor, status, now);

		return hasNext
			? CursorTemplate.ofWithNextCursor(nextCursor, dtos)
			: CursorTemplate.of(dtos);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? teamChallenge.id.lt(cursor) : null;
	}

	private boolean hasNextTeamChallenge(Long cursor, ChallengeStatus status, LocalDateTime now) {
		Integer fetchFirst = queryFactory
			.selectOne()
			.from(teamChallenge)
			.where(
				teamChallenge.id.lt(cursor),
				teamChallenge.challengeStatus.eq(status),
				teamChallenge.beginDateTime.loe(now),
				teamChallenge.endDateTime.goe(now)
			)
			.fetchFirst();

		return fetchFirst != null;
	}

	private ChallengeListResponseDto toChallengeListDto(TeamChallenge challenge) {
		return new ChallengeListResponseDto(
			challenge.getId(),
			challenge.getChallengeName(),
			challenge.getBeginDateTime(),
			challenge.getEndDateTime(),
			challenge.getChallengeImage(),
			challenge.getChallengePoint().getAmount().intValue()
		);
	}
} 