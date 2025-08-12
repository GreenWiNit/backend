package com.example.green.domain.certification.infra;

import static com.example.green.domain.certification.domain.QChallengeCertification.*;
import static com.example.green.domain.certification.exception.CertificationExceptionMessage.*;
import static com.example.green.domain.challenge.entity.group.QChallengeGroup.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.exception.CertificationException;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.global.api.page.CursorTemplate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChallengeCertificationQueryImpl implements ChallengeCertificationQuery {

	private final ChallengeCertificationRepository challengeCertificationRepository;
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId) {
		if (challengeCertificationRepository.existsByTeamChallenge(challengeId, challengeDate, memberId)) {
			throw new CertificationException(EXISTS_TEAM_CHALLENGE_CERT_OF_DAY);
		}
	}

	@Override
	public void checkAlreadyPersonalCert(Long challengeId, LocalDate challengeDate, Long memberId) {
		if (challengeCertificationRepository.existsByPersonalChallenge(challengeId, challengeDate, memberId)) {
			throw new CertificationException(EXISTS_TEAM_CHALLENGE_CERT_OF_DAY);
		}
	}

	@Override
	public CursorTemplate<String, ChallengeCertificationDto> findCertificationByPersonal(
		String cursor, Long memberId, Integer size
	) {
		List<ChallengeCertificationDto> result = jpaQueryFactory.select(
				Projections.constructor(ChallengeCertificationDto.class,
					challengeCertification.id,
					challengeCertification.challenge.challengeName,
					challengeCertification.certifiedDate,
					challengeCertification.status
				))
			.from(challengeCertification)
			.where(
				fromCondition(cursor),
				challengeCertification.member.memberId.eq(memberId),
				challengeCertification.challenge.type.eq(ChallengeSnapshot.PERSONAL_TYPE)
			)
			.orderBy(challengeCertification.certifiedDate.desc(), challengeCertification.id.desc())
			.limit(size + 1)
			.fetch();

		return CursorTemplate.from(result, size, dto -> dto.certifiedDate() + "," + dto.id());
	}

	public BooleanExpression fromCondition(String cursor) {
		if (cursor == null) {
			return null;
		}

		String[] parts = cursor.split(",");
		LocalDate dateCursor = LocalDate.parse(parts[0]);
		Long idCursor = Long.parseLong(parts[1]);
		return challengeCertification.certifiedDate.lt(dateCursor)
			.or(challengeCertification.certifiedDate.eq(dateCursor)
				.and(challengeGroup.id.lt(idCursor)));
	}
}
