package com.example.green.domain.challenge.repository;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challenge.controller.dto.admin.AdminPersonalChallengeListResponseDto;
import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PersonalChallengeRepositoryImpl implements PersonalChallengeRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeListResponseDto> findPersonalChallengesByCursor(
		Long cursor,
		int size,
		ChallengeStatus status,
		LocalDateTime now
	) {
		List<PersonalChallenge> challenges = queryFactory
			.selectFrom(personalChallenge)
			.where(
				cursorCondition(cursor),
				personalChallenge.challengeStatus.eq(status),
				personalChallenge.beginDateTime.loe(now),
				personalChallenge.endDateTime.goe(now)
			)
			.orderBy(personalChallenge.id.desc())
			.limit(size)
			.fetch();

		if (challenges.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<ChallengeListResponseDto> dtos = challenges.stream()
			.map(this::toChallengeListDto)
			.toList();

		Long nextCursor = challenges.getLast().getId();
		boolean hasNext = hasNextPersonalChallenge(nextCursor, status, now);

		if (hasNext) {
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	@Override
	public CursorTemplate<Long, AdminPersonalChallengeListResponseDto> findAllForAdminByCursor(Long cursor, int size) {
		List<PersonalChallenge> challenges = queryFactory
			.selectFrom(personalChallenge)
			.where(cursorCondition(cursor))
			.orderBy(personalChallenge.id.desc())
			.limit(size + 1)
			.fetch();

		if (challenges.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = challenges.size() > size;
		if (hasNext) {
			challenges = challenges.subList(0, size);
		}

		List<AdminPersonalChallengeListResponseDto> dtos = challenges.stream()
			.map(AdminPersonalChallengeListResponseDto::from)
			.toList();

		if (hasNext) {
			Long nextCursor = challenges.getLast().getId();
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? personalChallenge.id.lt(cursor) : null;
	}

	private boolean hasNextPersonalChallenge(Long cursor, ChallengeStatus status, LocalDateTime now) {
		Integer fetchFirst = queryFactory
			.selectOne()
			.from(personalChallenge)
			.where(
				personalChallenge.id.lt(cursor),
				personalChallenge.challengeStatus.eq(status),
				personalChallenge.beginDateTime.loe(now),
				personalChallenge.endDateTime.goe(now)
			)
			.fetchFirst();

		return fetchFirst != null;
	}

	private ChallengeListResponseDto toChallengeListDto(PersonalChallenge challenge) {
		return new ChallengeListResponseDto(
			challenge.getId(),
			challenge.getChallengeName(),
			challenge.getBeginDateTime(),
			challenge.getEndDateTime(),
			challenge.getChallengeImage(),
			challenge.getChallengePoint().getAmount().intValue()
		);
	}
}
