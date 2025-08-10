package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.entity.QTeamChallengeGroup.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeGroupParticipation.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.challengecert.repository.dao.ChallengeParticipantDao;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeamChallengeParticipationRepositoryImpl implements TeamChallengeParticipationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationsByCursor(
		Member member,
		Long cursor,
		int size
	) {
		List<TeamChallengeParticipation> participations = queryFactory
			.selectFrom(teamChallengeParticipation)
			.where(
				teamChallengeParticipation.memberId.eq(member.getId()),
				cursorCondition(cursor)
			)
			.orderBy(teamChallengeParticipation.id.desc())
			.limit(size)
			.fetch();

		if (participations.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<ChallengeListResponseDto> dtos = participations.stream()
			.map(p -> toChallengeListDto(p.getTeamChallenge()))
			.toList();

		Long nextCursor = participations.getLast().getId();
		boolean hasNext = hasNextParticipation(member, nextCursor);

		return hasNext
			? CursorTemplate.ofWithNextCursor(nextCursor, dtos)
			: CursorTemplate.of(dtos);
	}

	@Override
	public CursorTemplate<Long, ChallengeParticipantDao> findParticipantsByChallengeIdCursor(
		Long challengeId,
		Long cursor,
		int size
	) {
		List<ChallengeParticipantDao> daos = queryFactory
			.select(Projections.constructor(ChallengeParticipantDao.class,
				teamChallengeParticipation.memberId,
				// todo:
				teamChallengeParticipation.participatedAt,
				teamChallengeGroup.teamCode, // 실제 teamCode 조회
				teamChallengeGroupParticipation.createdDate, // 팀 선택 일시
				Expressions.nullExpression(Integer.class) // certificationCount (추후 구현시 사용)
			))
			.from(teamChallengeParticipation)
			.leftJoin(teamChallengeGroupParticipation)
			.on(teamChallengeGroupParticipation.teamChallengeParticipation.eq(teamChallengeParticipation))
			.leftJoin(teamChallengeGroup)
			.on(teamChallengeGroupParticipation.teamChallengeGroup.eq(teamChallengeGroup))
			.where(
				teamChallengeParticipation.teamChallenge.id.eq(challengeId),
				cursorCondition(cursor)
			)
			.orderBy(teamChallengeParticipation.id.desc())
			.limit(size + 1)
			.fetch();

		if (daos.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = daos.size() > size;
		List<ChallengeParticipantDao> result = hasNext ? daos.subList(0, size) : daos;

		if (hasNext) {
			// 마지막 요소의 ID를 커서로 사용하기 위해 추가 조회 필요
			Long nextCursor = queryFactory
				.select(teamChallengeParticipation.id)
				.from(teamChallengeParticipation)
				.where(
					teamChallengeParticipation.teamChallenge.id.eq(challengeId),
					cursorCondition(cursor)
				)
				.orderBy(teamChallengeParticipation.id.desc())
				.offset(size - 1)
				.limit(1)
				.fetchOne();

			return CursorTemplate.ofWithNextCursor(nextCursor, result);
		} else {
			return CursorTemplate.of(result);
		}
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? teamChallengeParticipation.id.lt(cursor) : null;
	}

	private boolean hasNextParticipation(Member member, Long cursor) {
		Integer fetchFirst = queryFactory
			.selectOne()
			.from(teamChallengeParticipation)
			.where(
				teamChallengeParticipation.memberId.eq(member.getId()),
				teamChallengeParticipation.id.lt(cursor)
			)
			.fetchFirst();

		return fetchFirst != null;
	}

	private ChallengeListResponseDto toChallengeListDto(
		com.example.green.domain.challenge.entity.TeamChallenge challenge) {
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
