package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamChallengeQueryImpl implements TeamChallengeQuery {

	private final JPAQueryFactory queryFactory;

	public CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(
		Long memberId,
		Long cursor,
		int size
	) {
		List<ChallengeListResponseDto> participation = queryFactory
			.select(TeamChallengeProjections.toChallenges())
			.from(teamChallenge)
			.join(teamChallengeParticipation.teamChallenge)
			.where(
				teamChallengeParticipation.member.id.eq(memberId),
				cursorCondition(cursor)
			)
			.orderBy(teamChallengeParticipation.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(participation, size, ChallengeListResponseDto::id);
	}

	public CursorTemplate<Long, ChallengeListResponseDto> findTeamChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		List<ChallengeListResponseDto> participation = queryFactory
			.select(TeamChallengeProjections.toChallenges())
			.from(teamChallenge)
			.where(
				cursorCondition(cursor),
				teamChallenge.challengeStatus.eq(status),
				teamChallenge.beginDateTime.loe(now),
				teamChallenge.endDateTime.goe(now)
			)
			.orderBy(teamChallenge.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(participation, size, ChallengeListResponseDto::id);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return personalChallengeParticipation.id.lt(cursor);
	}
}
