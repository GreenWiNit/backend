package com.example.green.domain.challenge.infra.querydsl.executor;

import static com.example.green.domain.challenge.entity.challenge.QChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;
import static com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.infra.querydsl.projections.ChallengeClientProjections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChallengeClientQueryExecutor {

	private final JPAQueryFactory queryFactory;

	public List<ChallengeDto> executeParticipationQuery(BooleanExpression condition, int size) {
		return queryFactory.select(ChallengeClientProjections.toMyChallenges())
			.from(challenge)
			.join(challenge.participations, participation)
			.where(condition)
			.orderBy(participation.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public List<ChallengeDto> executeChallengesQuery(BooleanExpression condition, int size) {
		return queryFactory.select(ChallengeClientProjections.toChallenges())
			.from(challenge)
			.where(condition)
			.orderBy(challenge.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public ChallengeDetailDtoV2 executeChallengeDetailQuery(BooleanExpression condition, Long id) {
		return queryFactory.select(ChallengeClientProjections.toChallengeByMember(condition))
			.from(challenge)
			.where(challenge.id.eq(id), challenge.display.eq(VISIBLE))
			.fetchOne();
	}
}
