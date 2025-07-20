package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challenge.entity.QPersonalChallenge.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeCertification.*;
import static com.example.green.domain.challengecert.entity.QPersonalChallengeParticipation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;
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
		return ChallengeCertificationListResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getPersonalChallenge().getId())
			.challengeTitle(certification.getParticipation().getPersonalChallenge().getChallengeName())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}
}
