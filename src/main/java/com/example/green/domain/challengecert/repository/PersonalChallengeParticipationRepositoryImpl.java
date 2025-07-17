package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.*;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challenge.controller.dto.ChallengeListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeParticipation;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 개인 챌린지 참여 정보 조회를 위한 커스텀 레포지토리 구현체
 */
@Repository
@RequiredArgsConstructor
public class PersonalChallengeParticipationRepositoryImpl implements PersonalChallengeParticipationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeListResponseDto> findMyParticipationsByCursor(
		Member member,
		Long cursor,
		int size
	) {
		List<PersonalChallengeParticipation> participations = queryFactory
			.selectFrom(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.member.eq(member),
				cursorCondition(cursor)
			)
			.orderBy(personalChallengeParticipation.id.desc())
			.limit(size)
			.fetch();

		if (participations.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		List<ChallengeListResponseDto> dtos = participations.stream()
			.map(p -> toChallengeListDto(p.getPersonalChallenge()))
			.toList();

		Long nextCursor = participations.getLast().getId();
		boolean hasNext = hasNextParticipation(member, nextCursor);

		return hasNext
			? CursorTemplate.ofWithNextCursor(nextCursor, dtos)
			: CursorTemplate.of(dtos);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? personalChallengeParticipation.id.lt(cursor) : null;
	}

	private boolean hasNextParticipation(Member member, Long cursor) {
		Integer fetchFirst = queryFactory
			.selectOne()
			.from(personalChallengeParticipation)
			.where(
				personalChallengeParticipation.member.eq(member),
				personalChallengeParticipation.id.lt(cursor)
			)
			.fetchFirst();

		return fetchFirst != null;
	}

	private ChallengeListResponseDto toChallengeListDto(
		com.example.green.domain.challenge.entity.PersonalChallenge challenge) {
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
