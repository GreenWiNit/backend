package com.example.green.domain.challenge.infra.querydsl;

import static com.example.green.domain.challenge.entity.challenge.QPersonalChallenge.*;
import static com.example.green.domain.challenge.entity.challenge.QPersonalChallengeParticipation.*;
import static com.example.green.domain.challenge.exception.ChallengeExceptionMessage.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.ChallengeDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminChallengeDetailDto;
import com.example.green.domain.challenge.controller.query.dto.challenge.AdminPersonalChallengesDto;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeStatus;
import com.example.green.domain.challenge.exception.ChallengeException;
import com.example.green.domain.challenge.infra.querydsl.projections.PersonalChallengeProjections;
import com.example.green.domain.challenge.repository.PersonalChallengeRepository;
import com.example.green.domain.challenge.repository.query.PersonalChallengeQuery;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
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

	public CursorTemplate<Long, ChallengeDto> findMyParticipationByCursor(
		Long memberId,
		Long cursor,
		int size
	) {
		List<ChallengeDto> participation = queryFactory
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

		return CursorTemplate.from(participation, size, ChallengeDto::id);
	}

	public CursorTemplate<Long, ChallengeDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		List<ChallengeDto> participation = queryFactory
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

		return CursorTemplate.from(participation, size, ChallengeDto::id);
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

	public AdminChallengeDetailDto getChallengeDetail(Long challengeId) {
		PersonalChallenge personalChallenge = getPersonalChallengeById(challengeId);
		return AdminChallengeDetailDto.from(personalChallenge);
	}

	@Override
	public PageTemplate<AdminPersonalChallengesDto> findChallengePage(Integer page, Integer size) {
		long count = personalChallengeRepository.count();
		Pagination pagination = Pagination.of(count, page, size);

		List<AdminPersonalChallengesDto> result = queryFactory
			.select(PersonalChallengeProjections.toChallengesForAdmin())
			.from(personalChallenge)
			.orderBy(personalChallenge.id.desc())
			.offset(pagination.calculateOffset())
			.limit(pagination.getPageSize())
			.fetch();

		return PageTemplate.of(result, pagination);
	}

	@Override
	public List<AdminPersonalChallengesDto> findChallengePageForExcel() {
		return queryFactory
			.select(PersonalChallengeProjections.toChallengesForAdmin())
			.from(personalChallenge)
			.orderBy(personalChallenge.id.desc())
			.fetch();
	}

	private BooleanExpression cursorCondition(Long cursor) {
		if (cursor == null) {
			return null;
		}
		return personalChallenge.id.lt(cursor);
	}
}
