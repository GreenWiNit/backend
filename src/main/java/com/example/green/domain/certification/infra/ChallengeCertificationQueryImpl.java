package com.example.green.domain.certification.infra;

import static com.example.green.domain.certification.exception.CertificationExceptionMessage.*;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

import com.example.green.domain.certification.domain.ChallengeCertificationQuery;
import com.example.green.domain.certification.domain.ChallengeCertificationRepository;
import com.example.green.domain.certification.exception.CertificationException;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ChallengeCertificationQueryImpl implements ChallengeCertificationQuery {

	private final ChallengeCertificationRepository challengeCertificationRepository;

	@Override
	public void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId) {
		if (challengeCertificationRepository.existsByTeamChallenge(challengeId, challengeDate, memberId)) {
			throw new CertificationException(EXISTS_TEAM_CHALLENGE_CERT_OF_DAY);
		}
	}
}
