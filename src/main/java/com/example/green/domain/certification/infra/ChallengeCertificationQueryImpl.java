package com.example.green.domain.certification.infra;

import static com.example.green.domain.certification.exception.CertificationExceptionMessage.*;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.green.domain.certification.domain.ChallengeCertification;
import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.domain.ChallengeSnapshot;
import com.example.green.domain.certification.exception.CertificationException;
import com.example.green.domain.certification.exception.CertificationExceptionMessage;
import com.example.green.domain.certification.infra.executor.ChallengeCertificationQueryExecutor;
import com.example.green.domain.certification.infra.filter.ChallengeCertificationFilter;
import com.example.green.domain.certification.infra.predicates.ChallengeCertificationPredicates;
import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDetailDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;
import com.example.green.global.api.page.Pagination;
import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChallengeCertificationQueryImpl implements ChallengeCertificationQuery {

	private final ChallengeCertificationRepository challengeCertificationRepository;
	private final ChallengeCertificationQueryExecutor executor;

	public void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId) {
		if (challengeCertificationRepository.existsByTeamChallenge(challengeId, challengeDate, memberId)) {
			throw new CertificationException(EXISTS_CHALLENGE_CERT_OF_DAY);
		}
	}

	public void checkAlreadyPersonalCert(Long challengeId, LocalDate challengeDate, Long memberId) {
		if (challengeCertificationRepository.existsByPersonalChallenge(challengeId, challengeDate, memberId)) {
			throw new CertificationException(EXISTS_CHALLENGE_CERT_OF_DAY);
		}
	}

	public CursorTemplate<String, ChallengeCertificationDto> findCertByPersonal(
		String cursor, Long memberId, Integer size, String type
	) {
		validateChallengeType(type);
		List<ChallengeCertificationDto> result = executor.executeCertByPersonalQuery(cursor, memberId, size, type);

		return CursorTemplate.from(result, size, dto -> dto.certifiedDate() + "," + dto.id());
	}

	private static void validateChallengeType(String type) {
		if (!ChallengeSnapshot.isValidType(type)) {
			throw new CertificationException(INVALID_CHALLENGE_TYPE);
		}
	}

	public ChallengeCertification getCertificationById(Long certificationId) {
		return challengeCertificationRepository.findById(certificationId)
			.orElseThrow(() -> new CertificationException(NOT_FOUND_CHALLENGE_CERTIFICATION));
	}

	public ChallengeCertificationDetailDto findCertificationDetail(Long certificationId, Long memberId) {
		ChallengeCertification certification = getCertificationById(certificationId);
		if (!certification.getMember().getMemberId().equals(memberId)) {
			throw new CertificationException(CertificationExceptionMessage.INVALID_ACCESS);
		}
		return ChallengeCertificationDetailDto.from(certification);
	}

	public PageTemplate<AdminCertificateSearchDto> search(ChallengeCertificationFilter filter) {
		validateChallengeType(filter.type());

		BooleanExpression expression = ChallengeCertificationPredicates.searchCondition(filter);
		long count = executor.executeSearchCountQuery(expression);

		Pagination pagination = Pagination.fromCondition(filter, count);
		List<AdminCertificateSearchDto> content = executor.executeSearchQuery(expression, pagination);

		return PageTemplate.of(content, pagination);
	}

	public int getTotalCertifiedCountByMember(Long memberId) {
		return challengeCertificationRepository.countChallengeCertificationByMemberMemberId(memberId);
	}
}
