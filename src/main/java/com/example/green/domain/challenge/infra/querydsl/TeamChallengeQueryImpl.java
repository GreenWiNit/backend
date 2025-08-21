package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.entity.challenge.QTeamChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QTeamChallengeParticipation.*;
import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.infra.querydsl.projections.TeamChallengeProjections;
import com.example.green.domain.challenge.repository.TeamChallengeRepository;
import com.example.green.domain.challenge.repository.query.TeamChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.example.green.infra.database.querydsl.BooleanExpressionConnector;
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

	public CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(
		Long memberId,
		Long cursor,
		int size,
		LocalDateTime now
	) {
		List<ChallengeDto> participation = queryFactory
			.select(TeamChallengeProjections.toChallenges())
			.from(teamChallenge)
			.join(teamChallenge.participations, teamChallengeParticipation)
			.where(BooleanExpressionConnector.combineWithAnd(
				activeChallengeCondition(cursor, now),
				teamChallengeParticipation.memberId.eq(memberId)
			))
			.orderBy(teamChallengeParticipation.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(participation, size, ChallengeDto::id);
	}

	public CursorTemplate<Long, ChallengeDto> findTeamChallengesByCursor(
		Long cursor,
		int size,
		LocalDateTime now
	) {
		List<ChallengeDto> participation = queryFactory
			.select(TeamChallengeProjections.toChallenges())
			.from(teamChallenge)
			.where(activeChallengeCondition(cursor, now))
			.orderBy(teamChallenge.id.desc())
			.limit(size + 1)
			.fetch();
		return CursorTemplate.from(participation, size, ChallengeDto::cursor);
	}

	public ChallengeDetailDto findTeamChallenge(Long challengeId, Long memberId) {
		BooleanExpression exists = fromCondition(challengeId, memberId);

		return Optional.ofNullable(queryFactory
				.select(TeamChallengeProjections.toChallengeByMember(exists))
				.from(teamChallenge)
				.where(teamChallenge.id.eq(challengeId), teamChallenge.displayStatus.eq(ChallengeDisplayStatus.VISIBLE))
				.fetchOne())
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	public TeamChallenge getTeamChallengeById(Long challengeId) {
		return teamChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	public PageTemplate<AdminTeamChallengesDto> findChallengePage(Integer page, Integer size) {
		long count = teamChallengeRepository.count();
		Pagination pagination = Pagination.of(count, page, size);

		List<AdminTeamChallengesDto> result = queryFactory
			.select(TeamChallengeProjections.toChallengesForAdmin())
			.from(teamChallenge)
			.orderBy(teamChallenge.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}

	@Override
	public AdminChallengeDetailDto getChallengeDetail(Long challengeId) {
		TeamChallenge teamChallenge = getTeamChallengeById(challengeId);
		return AdminChallengeDetailDto.from(teamChallenge);
	}

	public List<AdminTeamChallengesDto> findTeamChallengeForExcel() {
		return queryFactory
			.select(TeamChallengeProjections.toChallengesForAdmin())
			.from(teamChallenge)
			.orderBy(teamChallenge.id.desc())
			.fetch();
	}

	public void validateGroupPeriod(Long challengeId, LocalDate challengeDate) {
		if (!teamChallengeRepository.isGroupPeriodValidForChallenge(challengeId, challengeDate)) {
			throw new ChallengeException(MISMATCH_GROUP_PERIOD_RANGE);
		}
	}

	@Override
	public ChallengeDetailDtoV2 findTeamChallengeV2(Long challengeId, Long memberId) {
		BooleanExpression exists = fromCondition(challengeId, memberId);
		return Optional.ofNullable(queryFactory
				.select(TeamChallengeProjections.toChallengeByMemberV2(exists))
				.from(teamChallenge)
				.where(teamChallenge.id.eq(challengeId), teamChallenge.displayStatus.eq(ChallengeDisplayStatus.VISIBLE))
				.fetchOne())
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	private static BooleanExpression fromCondition(Long challengeId, Long memberId) {
		BooleanExpression exists = JPAExpressions.selectOne()
			.from(teamChallengeParticipation)
			.where(
				teamChallengeParticipation.teamChallenge.id.eq(challengeId),
				teamChallengeParticipation.memberId.eq(memberId)
			).exists();
		return exists;
	}

	private BooleanExpression activeChallengeCondition(Long cursor, LocalDateTime now) {
		return BooleanExpressionConnector.combineWithAnd(
			teamChallenge.displayStatus.eq(ChallengeDisplayStatus.VISIBLE),
			teamChallenge.beginDate.loe(now.toLocalDate()),
			teamChallenge.endDate.goe(now.toLocalDate()),
			cursorCondition(cursor)
		);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return teamChallenge.id.lt(cursor);
	}
}
