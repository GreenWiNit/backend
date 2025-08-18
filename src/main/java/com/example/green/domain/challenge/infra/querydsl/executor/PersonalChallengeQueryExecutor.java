package com.example.green.domain.challenge.infra.querydsl.executor;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalParticipationDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDtoV2;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;
import com.example.green.domain.challenge.infra.querydsl.projections.PersonalChallengeProjections;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonalChallengeQueryExecutor {

	private final JPAQueryFactory queryFactory;

	public List<ChallengeDto> executeParticipationQueryForClient(BooleanExpression condition, int size) {
		return queryFactory.select(PersonalChallengeProjections.toChallenges())
			.from(personalChallenge)
			.join(personalChallenge.participations, personalChallengeParticipation)
			.where(condition)
			.orderBy(personalChallengeParticipation.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public List<ChallengeDto> executeChallengesQueryForClient(BooleanExpression condition, int size) {
		return queryFactory.select(PersonalChallengeProjections.toChallenges())
			.from(personalChallenge)
			.where(condition)
			.orderBy(personalChallenge.id.desc())
			.limit(size + 1)
			.fetch();
	}

	public ChallengeDetailDto executeChallengeDetailQuery(BooleanExpression participationExists, Long challengeId) {
		return queryFactory.select(PersonalChallengeProjections.toChallengeByMember(participationExists))
			.from(personalChallenge)
			.where(personalChallenge.id.eq(challengeId),
				personalChallenge.displayStatus.eq(ChallengeDisplayStatus.VISIBLE))
			.fetchOne();
	}

	public Optional<ChallengeDetailDtoV2> executeChallengeDetailQueryV2(BooleanExpression participationExists,
		Long challengeId) {
		return Optional.ofNullable(
			queryFactory.select(PersonalChallengeProjections.toChallengeByMemberV2(participationExists))
				.from(personalChallenge)
				.where(personalChallenge.id.eq(challengeId))
				.fetchOne());
	}

	public List<AdminPersonalChallengesDto> executeChallengesQueryForAdmin(Pagination pagination) {
		return queryFactory
			.select(PersonalChallengeProjections.toChallengesForAdmin())
			.from(personalChallenge)
			.orderBy(personalChallenge.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<AdminPersonalChallengesDto> executeChallengeExcelQueryForAdmin() {
		return queryFactory
			.select(PersonalChallengeProjections.toChallengesForAdmin())
			.from(personalChallenge)
			.orderBy(personalChallenge.id.desc())
			.fetch();
	}

	public List<AdminPersonalParticipationDto> executeParticipantQueryForAdmin(
		Pagination pagination, Long challengeId
	) {
		return queryFactory
			.select(PersonalChallengeProjections.toParticipationForAdmin())
			.from(personalChallengeParticipation)
			.where(personalChallengeParticipation.personalChallenge.id.eq(challengeId))
			.orderBy(personalChallengeParticipation.participatedAt.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();
	}

	public List<AdminPersonalParticipationDto> executeParticipantQueryForExcel(Long challengeId) {
		return queryFactory
			.select(PersonalChallengeProjections.toParticipationForAdmin())
			.from(personalChallengeParticipation)
			.where(personalChallengeParticipation.personalChallenge.id.eq(challengeId))
			.orderBy(personalChallengeParticipation.participatedAt.desc())
			.fetch();
	}
}
