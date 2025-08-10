package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;
import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengesDto;
import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamChallengeQueryImpl implements TeamChallengeQuery {

	private final JPAQueryFactory queryFactory;
	private final TeamChallengeRepository teamChallengeRepository;

	public CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(
		Long memberId,
		Long cursor,
		int size
	) {
		List<ChallengeListResponseDto> participation = queryFactory
			.select(TeamChallengeProjections.toChallenges())
			.from(teamChallenge)
			.join(teamChallenge.participations, teamChallengeParticipation)
			.where(
				teamChallengeParticipation.memberId.eq(memberId),
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

	public ChallengeDetailDto findTeamChallenge(Long challengeId, Long memberId) {
		BooleanExpression exists = JPAExpressions.selectOne()
			.from(teamChallengeParticipation)
			.where(
				teamChallengeParticipation.teamChallenge.id.eq(challengeId),
				teamChallengeParticipation.memberId.eq(memberId)
			).exists();

		return queryFactory
			.select(TeamChallengeProjections.toChallengeByMember(exists))
			.from(teamChallenge)
			.where(teamChallenge.id.eq(challengeId))
			.fetchOne();
	}

	public TeamChallenge getTeamChallengeById(Long challengeId) {
		return teamChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	public CursorTemplate<Long, AdminTeamChallengesDto> findAllForAdminByCursor(Long cursor, Integer size) {
		List<AdminTeamChallengesDto> challenges = queryFactory
			.select(TeamChallengeProjections.toChallengesForAdmin())
			.from(teamChallenge)
			.where(cursorCondition(cursor))
			.orderBy(teamChallenge.id.desc())
			.limit(size + 1)
			.fetch();
		return CursorTemplate.from(challenges, size, AdminTeamChallengesDto::id);
	}

	@Override
	public AdminChallengeDetailDto getChallengeDetail(Long challengeId) {
		TeamChallenge teamChallenge = getTeamChallengeById(challengeId);
		return AdminChallengeDetailDto.from(teamChallenge);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return teamChallenge.id.lt(cursor);
	}
}
