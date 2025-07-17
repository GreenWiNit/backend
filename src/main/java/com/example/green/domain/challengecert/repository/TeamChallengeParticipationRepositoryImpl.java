package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.teamChallengeParticipation;

import java.util.List;

import com.example.green.domain.challengecert.entity.TeamChallengeParticipation;
import com.example.green.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 팀 챌린지 참여 정보 조회를 위한 커스텀 레포지토리 구현체
 */
@RequiredArgsConstructor
public class TeamChallengeParticipationRepositoryImpl implements TeamChallengeParticipationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TeamChallengeParticipation> findMyParticipationsByCursor(
        Member member,
        Long cursor,
        int size
    ) {
        return queryFactory
            .selectFrom(teamChallengeParticipation)
            .where(
                teamChallengeParticipation.member.eq(member),
                cursorGt(cursor)
            )
            .orderBy(teamChallengeParticipation.id.asc())
            .limit(size)
            .fetch();
    }

    @Override
    public boolean existsNextParticipation(
        Member member,
        Long cursor
    ) {
        Integer fetchFirst = queryFactory
            .selectOne()
            .from(teamChallengeParticipation)
            .where(
                teamChallengeParticipation.member.eq(member),
                teamChallengeParticipation.id.gt(cursor)
            )
            .fetchFirst();

        return fetchFirst != null;
    }

    private BooleanExpression cursorGt(Long cursor) {
        return cursor != null ? teamChallengeParticipation.id.gt(cursor) : null;
    }
} 