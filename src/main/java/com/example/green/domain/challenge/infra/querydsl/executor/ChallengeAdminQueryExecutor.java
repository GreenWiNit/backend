package com.example.green.domain.challenge.infra.querydsl.executor;

import static com.example.green.domain.challenge.entity.challenge.QChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QParticipation.*;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.example.green.domain.challenge.infra.querydsl.projections.ChallengeAdminProjections;
import com.example.green.global.api.page.Pagination;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChallengeAdminQueryExecutor {

	private final JPAQueryFactory queryFactory;

	public List<AdminChallengesDto> executeChallengePageQuery(Pagination pagination, ChallengeType type) {
		return queryFactory
			.select(ChallengeAdminProjections.toChallengePage())
			.from(challenge)
			.where(challenge.type.eq(type))
			.orderBy(challenge.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<AdminChallengesDto> executeChallengePageExcelQuery(ChallengeType type) {
		// TODO: 이후 데이터 수량이 많아지면 개선하기
		return queryFactory
			.select(ChallengeAdminProjections.toChallengePage())
			.from(challenge)
			.where(challenge.type.eq(type))
			.orderBy(challenge.id.desc())
			.fetch();
	}

	public List<AdminPersonalParticipationDto> executeParticipantQuery(Pagination pagination, Long id) {
		return queryFactory
			.select(ChallengeAdminProjections.toParticipation())
			.from(participation)
			.where(participation.challenge.id.eq(id))
			.orderBy(participation.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<AdminPersonalParticipationDto> executeParticipantQueryForExcel(Long challengeId) {
		return queryFactory
			.select(ChallengeAdminProjections.toParticipation())
			.from(participation)
			.where(participation.challenge.id.eq(challengeId))
			.orderBy(participation.createdDate.desc())
			.fetch();
	}
}
