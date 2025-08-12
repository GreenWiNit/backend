package com.example.green.domain.certification.domain;

import java.time.LocalDate;

public interface ChallengeCertificationQuery {
	void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId);

	void checkAlreadyPersonalCert(Long aLong, LocalDate localDate, Long aLong1);
}
