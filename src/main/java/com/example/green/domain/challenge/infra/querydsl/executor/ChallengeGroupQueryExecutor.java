package com.example.green.domain.challenge.infra.querydsl.executor;

import static com.example.green.domain.challenge.entity.challenge.QTeamChallengeParticipation.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroupParticipation.*;
import static com.example.green.domain.challenge.infra.querydsl.projections.ChallengeGroupProjections.*;
import static com.querydsl.core.group.GroupBy.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminTeamParticipantDto;
import com.example.green.domain.challenge.controller.query.dto.group.AdminChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.ChallengeGroupDto;
import com.example.green.domain.challenge.controller.query.dto.group.MyChallengeGroupDto;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChallengeGroupQueryExecutor {

	private final JPAQueryFactory queryFactory;

	public List<Long> executeMyGroupIdsQuery(Long memberId) {
		return queryFactory.select(challengeGroupParticipation.challengeGroup.id)
			.from(challengeGroupParticipation)
			.where(challengeGroupParticipation.memberId.eq(memberId))
			.fetch();
	}

	public Map<Long, Boolean> executeMyGroupCertifiedMap(Long memberId) {
		List<Tuple> results = queryFactory
			.select(challengeGroupParticipation.challengeGroup.id, challengeGroupParticipation.certified)
			.from(challengeGroupParticipation)
			.where(challengeGroupParticipation.memberId.eq(memberId))
			.fetch();

		return results.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(challengeGroupParticipation.challengeGroup.id),
				tuple -> tuple.get(challengeGroupParticipation.certified)
			));
	}

	public List<MyChallengeGroupDto> executeMyGroupQuery(Integer size, Long memberId, BooleanExpression condition) {
		return queryFactory.select(toMyGroup(memberId))
			.from(challengeGroup)
			.where(condition)
			.orderBy(challengeGroup.createdDate.desc(), challengeGroup.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public List<AdminChallengeGroupDto> executeGroupPageForAdmin(Pagination pagination) {
		return queryFactory.select(toGroupPageForAdmin())
			.from(challengeGroup)
			.orderBy(challengeGroup.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<ChallengeGroupDto> executeGroupCursorQuery(Integer size, BooleanExpression condition) {
		return queryFactory.select(toChallengeGroup())
			.from(challengeGroup)
			.where(condition)
			.orderBy(challengeGroup.createdDate.desc(), challengeGroup.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public long executeParticipantCountQuery(Long challengeId) {
		return Optional.ofNullable(createBaseParticipantQuery(challengeId)
			.select(challengeGroupParticipation.count())
			.fetchOne()
		).orElseThrow(() -> new IllegalStateException("팀 챌린지 참가자 카운트 실패"));
	}

	public List<AdminTeamParticipantDto> executeGroupParticipantQuery(Long challengeId, Pagination pagination) {
		return createBaseParticipantQuery(challengeId).select(toParticipantByChallenge())
			.orderBy(challengeGroupParticipation.createdDate.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<AdminTeamParticipantDto> executeGroupParticipantForExcelQuery(Long challengeId) {
		return createBaseParticipantQuery(challengeId).select(toParticipantByChallenge())
			.orderBy(challengeGroupParticipation.createdDate.desc())
			.fetch();
	}

	private JPAQuery<?> createBaseParticipantQuery(Long challengeId) {
		return queryFactory.from(challengeGroup)
			.join(challengeGroup.participants, challengeGroupParticipation)
			.join(teamChallengeParticipation)
			.on(teamChallengeParticipation.teamChallenge.id.eq(challengeId)
				.and(teamChallengeParticipation.memberId.eq(challengeGroupParticipation.memberId)))
			.where(challengeGroup.teamChallengeId.eq(challengeId));
	}

	public Map<Long, Long> executeCountByChallengeIds(List<Long> challengeIds) {
		return queryFactory.select(challengeGroup.teamChallengeId, challengeGroup.count())
			.from(challengeGroup)
			.where(challengeGroup.teamChallengeId.in(challengeIds))
			.groupBy(challengeGroup.teamChallengeId)
			.transform(groupBy(challengeGroup.teamChallengeId).as(challengeGroup.count()));
	}
}
