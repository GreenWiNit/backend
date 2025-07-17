package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.personalChallengeParticipation;

import java.util.List;

import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 개인 챌린지 참여 정보 조회를 위한 커스텀 레포지토리 구현체
 */
@RequiredArgsConstructor
public class PersonalChallengeParticipationRepositoryImpl implements PersonalChallengeParticipationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PersonalChallengeParticipation> findMyParticipationsByCursor(
        Member member,
        Long cursor,
        int size
    ) {
        return queryFactory
            .selectFrom(personalChallengeParticipation)
            .where(
                personalChallengeParticipation.member.eq(member),
                cursorGt(cursor)
            )
            .orderBy(personalChallengeParticipation.id.asc())
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
            .from(personalChallengeParticipation)
            .where(
                personalChallengeParticipation.member.eq(member),
                personalChallengeParticipation.id.gt(cursor)
            )
            .fetchFirst();

        return fetchFirst != null;
    }

    private BooleanExpression cursorGt(Long cursor) {
        return cursor != null ? personalChallengeParticipation.id.gt(cursor) : null;
    }
} 