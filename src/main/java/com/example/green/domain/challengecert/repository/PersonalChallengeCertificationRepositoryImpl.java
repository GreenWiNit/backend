package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeCertification.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.*;
import static com.example.green.domain.member.entity.QMember.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challengecert.dto.AdminParticipantMemberKeyResponseDto;
import com.example.green.domain.challengecert.dto.AdminPersonalCertificationSearchRequestDto;
import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.enums.CertificationStatus;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PersonalChallengeCertificationRepositoryImpl implements PersonalChallengeCertificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> findByMemberWithCursor(
		Member member,
		Long cursor,
		int size
	) {
		List<PersonalChallengeCertification> certifications = queryFactory
			.selectFrom(personalChallengeCertification)
			.join(personalChallengeCertification.participation, personalChallengeParticipation).fetchJoin()
			.join(personalChallengeParticipation.personalChallenge, personalChallenge).fetchJoin()
			.where(
				personalChallengeParticipation.member.eq(member),
				cursorCondition(cursor)
			)
			.orderBy(personalChallengeCertification.id.desc())
			.limit(size + 1)
			.fetch();

		if (certifications.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = certifications.size() > size;
		if (hasNext) {
			certifications = certifications.subList(0, size);
		}

		List<ChallengeCertificationListResponseDto> dtos = certifications.stream()
			.map(this::toCertificationListDto)
			.toList();

		if (hasNext) {
			Long nextCursor = certifications.getLast().getId();
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	@Override
	public Optional<PersonalChallengeCertification> findByIdAndMember(Long id, Member member) {
		PersonalChallengeCertification certification = queryFactory
			.selectFrom(personalChallengeCertification)
			.join(personalChallengeCertification.participation, personalChallengeParticipation).fetchJoin()
			.join(personalChallengeParticipation.personalChallenge, personalChallenge).fetchJoin()
			.where(
				personalChallengeCertification.id.eq(id),
				personalChallengeParticipation.member.eq(member)
			)
			.fetchOne();

		return Optional.ofNullable(certification);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? personalChallengeCertification.id.lt(cursor) : null;
	}

	private ChallengeCertificationListResponseDto toCertificationListDto(PersonalChallengeCertification certification) {
		return ChallengeCertificationListResponseDto.fromPersonalChallengeCertification(certification);
	}

	@Override
	public List<AdminParticipantMemberKeyResponseDto> findParticipantMemberKeysByChallengeId(Long challengeId) {
		return queryFactory
			.select(Projections.constructor(AdminParticipantMemberKeyResponseDto.class,
				member.memberKey,
				member.profile.nickname
			))
			.from(personalChallengeCertification)
			.join(personalChallengeCertification.participation, personalChallengeParticipation)
			.join(personalChallengeParticipation.member, member)
			.where(personalChallengeParticipation.personalChallenge.id.eq(challengeId))
			.distinct()
			.fetch();
	}

	@Override
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> findPersonalCertificationsWithFilters(
		AdminPersonalCertificationSearchRequestDto searchRequest) {
		
		List<PersonalChallengeCertification> certifications = queryFactory
			.selectFrom(personalChallengeCertification)
			.join(personalChallengeCertification.participation, personalChallengeParticipation).fetchJoin()
			.join(personalChallengeParticipation.personalChallenge, personalChallenge).fetchJoin()
			.join(personalChallengeParticipation.member, member).fetchJoin()
			.where(
				challengeCondition(searchRequest.challengeId()),
				memberKeyCondition(searchRequest.memberKey()),
				statusCondition(searchRequest.statuses()),
				cursorCondition(searchRequest.cursor())
			)
			.orderBy(personalChallengeCertification.id.desc())
			.limit(searchRequest.size() + 1)
			.fetch();

		if (certifications.isEmpty()) {
			return CursorTemplate.ofEmpty();
		}

		boolean hasNext = certifications.size() > searchRequest.size();
		if (hasNext) {
			certifications = certifications.subList(0, searchRequest.size());
		}

		List<ChallengeCertificationListResponseDto> dtos = certifications.stream()
			.map(this::toCertificationListDto)
			.toList();

		if (hasNext) {
			Long nextCursor = certifications.getLast().getId();
			return CursorTemplate.ofWithNextCursor(nextCursor, dtos);
		} else {
			return CursorTemplate.of(dtos);
		}
	}

	/**
	 * 챌린지 ID 조건
	 */
	private BooleanExpression challengeCondition(Long challengeId) {
		return challengeId != null ? personalChallengeParticipation.personalChallenge.id.eq(challengeId) : null;
	}

	/**
	 * 참여자 memberKey 조건
	 */
	private BooleanExpression memberKeyCondition(String memberKey) {
		return (memberKey != null && !memberKey.trim().isEmpty()) 
			? member.memberKey.eq(memberKey.trim()) : null;
	}

	/**
	 * 인증 상태 조건
	 */
	private BooleanExpression statusCondition(List<CertificationStatus> statuses) {
		return (statuses != null && !statuses.isEmpty()) 
			? personalChallengeCertification.status.in(statuses) : null;
	}
}
