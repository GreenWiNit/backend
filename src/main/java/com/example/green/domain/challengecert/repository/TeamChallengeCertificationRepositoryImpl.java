package com.example.green.domain.challengecert.repository;

import static com.example.green.domain.challenge.entity.QTeamChallenge.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeCertification.*;
import static com.example.green.domain.challengecert.entity.QTeamChallengeParticipation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.green.domain.challengecert.dto.ChallengeCertificationListResponseDto;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.member.entity.Member;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TeamChallengeCertificationRepositoryImpl implements TeamChallengeCertificationRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public CursorTemplate<Long, ChallengeCertificationListResponseDto> findByMemberWithCursor(
		Member member,
		Long cursor,
		int size
	) {
		List<TeamChallengeCertification> certifications = queryFactory
			.selectFrom(teamChallengeCertification)
			.join(teamChallengeCertification.participation, teamChallengeParticipation).fetchJoin()
			.join(teamChallengeParticipation.teamChallenge, teamChallenge).fetchJoin()
			.where(
				teamChallengeParticipation.member.eq(member),
				cursorCondition(cursor)
			)
			.orderBy(teamChallengeCertification.id.desc())
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
	public Optional<TeamChallengeCertification> findByIdAndMember(Long id, Member member) {
		TeamChallengeCertification certification = queryFactory
			.selectFrom(teamChallengeCertification)
			.join(teamChallengeCertification.participation, teamChallengeParticipation).fetchJoin()
			.join(teamChallengeParticipation.teamChallenge, teamChallenge).fetchJoin()
			.where(
				teamChallengeCertification.id.eq(id),
				teamChallengeParticipation.member.eq(member)
			)
			.fetchOne();

		return Optional.ofNullable(certification);
	}

	private BooleanExpression cursorCondition(Long cursor) {
		return cursor != null ? teamChallengeCertification.id.lt(cursor) : null;
	}

	private ChallengeCertificationListResponseDto toCertificationListDto(TeamChallengeCertification certification) {
		return ChallengeCertificationListResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getTeamChallenge().getId())
			.challengeTitle(certification.getParticipation().getTeamChallenge().getChallengeName())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}
}
