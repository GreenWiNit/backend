package com.example.green.domain.challenge.infra;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;
import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.dto.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalChallengeQueryImpl implements PersonalChallengeQuery {

	private final PersonalChallengeRepository personalChallengeRepository;
	private final JPAQueryFactory queryFactory;

	public CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationByCursor(
		Long memberId,
		Long cursor,
		int size
	) {
		List<ChallengeListResponseDto> participation = queryFactory
			.select(PersonalChallengeProjections.toChallenges())
			.from(personalChallenge)
			.join(personalChallenge.participations, personalChallengeParticipation)
			.where(
				personalChallengeParticipation.memberId.eq(memberId),
				cursorCondition(cursor)
			)
			.orderBy(personalChallengeParticipation.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(participation, size, ChallengeListResponseDto::id);
	}

	public CursorTemplate<Long, ChallengeListResponseDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		List<ChallengeListResponseDto> participation = queryFactory
			.select(PersonalChallengeProjections.toChallenges())
			.from(personalChallenge)
			.where(
				cursorCondition(cursor),
				personalChallenge.challengeStatus.eq(status),
				personalChallenge.beginDateTime.loe(now),
				personalChallenge.endDateTime.goe(now)
			)
			.orderBy(personalChallenge.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(participation, size, ChallengeListResponseDto::id);
	}

	@Override
	public PersonalChallenge getPersonalChallengeById(Long challengeId) {
		return personalChallengeRepository.findById(challengeId)
			.orElseThrow(() -> new ChallengeException(CHALLENGE_NOT_FOUND));
	}

	@Override
	public ChallengeDetailDto findPersonalChallenge(Long challengeId, Long memberId) {
		BooleanExpression exists = JPAExpressions.selectOne()
			.from(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.personalChallenge.id.eq(challengeId),
				personalChallengeParticipation.memberId.eq(memberId)
			).exists();

		return queryFactory
			.select(PersonalChallengeProjections.toChallengeByMember(exists))
			.from(personalChallenge)
			.where(personalChallenge.id.eq(challengeId))
			.fetchOne();
	}

	public CursorTemplate<Long, AdminPersonalChallengesDto> findAllForAdminByCursor(Long cursor, Integer size) {
		List<AdminPersonalChallengesDto> challenges = queryFactory
			.select(PersonalChallengeProjections.toChallengesForAdmin())
			.from(personalChallenge)
			.where(cursorCondition(cursor))
			.orderBy(personalChallenge.id.desc())
			.limit(size + 1)
			.fetch();
		return CursorTemplate.from(challenges, size, AdminPersonalChallengesDto::id);
	}

	public AdminChallengeDetailDto getChallengeDetail(Long challengeId) {
		PersonalChallenge personalChallenge = getPersonalChallengeById(challengeId);
		return AdminChallengeDetailDto.from(personalChallenge);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return personalChallenge.id.lt(cursor);
	}
}
