package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminTeamChallengeListResponseDto;
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

		Long nextCursor = challenges.getLast().getId();
		boolean hasNext = hasNextTeamChallenge(nextCursor, status, now);

		if (hasNext) {
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	@Override
	public CursorTemplate<Long, AdminTeamChallengeListResponseDto> findAllForAdminByCursor(Long cursor, int size) {
		List<TeamChallenge> challenges = queryFactory
			.selectFrom(teamChallenge)
			.where(cursorCondition(cursor))
			.orderBy(teamChallenge.id.desc())
			.limit(size + 1)
			.fetch();

		if (challenges.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = challenges.size() > size;
		if (hasNext) {
			challenges = challenges.subList(0, size);
		}

		// 챌린지별 참여자 수 계산
		List<Long> challengeIds = challenges.stream()
			.map(TeamChallenge::getId)
			.toList();

		Map<Long, Long> participantCountMap = queryFactory
			.select(teamChallengeParticipation.teamChallenge.id, teamChallengeParticipation.count())
			.from(teamChallengeParticipation)
			.where(teamChallengeParticipation.teamChallenge.id.in(challengeIds))
			.groupBy(teamChallengeParticipation.teamChallenge.id)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(teamChallengeParticipation.teamChallenge.id),
				tuple -> tuple.get(teamChallengeParticipation.count())
			));

		List<AdminTeamChallengeListResponseDto> dtos = challenges.stream()
			.map(ch -> {
				Integer participantCount = participantCountMap.getOrDefault(ch.getId(), 0L).intValue();
				return AdminTeamChallengeListResponseDto.from(ch, participantCount);
			})
			.toList();

		if (hasNext) {
			Long nextCursor = challenges.getLast().getId();
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
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
			challenge.getChallengePoint()
		);
	}
}
