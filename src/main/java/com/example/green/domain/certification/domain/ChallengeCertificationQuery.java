package com.example.green.domain.certification.domain;

import java.time.LocalDate;

import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.global.api.page.CursorTemplate;

public interface ChallengeCertificationQuery {
	void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId);

	void checkAlreadyPersonalCert(Long aLong, LocalDate localDate, Long aLong1);

	CursorTemplate<String, ChallengeCertificationDto> findCertificationByPersonal(
		String cursor, Long memberId, Integer size);

	CursorTemplate<String, ChallengeCertificationDto> findCertificationByTeam(
		String cursor, Long memberId, Integer size);
}
