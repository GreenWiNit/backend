package com.example.green.domain.certification.domain;

import java.time.LocalDate;

import com.example.green.domain.certification.infra.filter.ChallengeCertificationFilter;
import com.example.green.domain.certification.ui.dto.AdminCertificateSearchDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDetailDto;
import com.example.green.domain.certification.ui.dto.ChallengeCertificationDto;
import com.example.green.global.api.page.CursorTemplate;
import com.example.green.global.api.page.PageTemplate;

public interface ChallengeCertificationQuery {
	void checkAlreadyTeamCert(Long challengeId, LocalDate challengeDate, Long memberId);

	void checkAlreadyPersonalCert(Long aLong, LocalDate localDate, Long aLong1);

	CursorTemplate<String, ChallengeCertificationDto> findCertByPersonal(
		String cursor, Long memberId, Integer size, String type);

	ChallengeCertification getCertificationById(Long certificationId);

	ChallengeCertificationDetailDto findCertificationDetail(Long certificationId, Long memberId);

	PageTemplate<AdminCertificateSearchDto> search(ChallengeCertificationFilter filter);

	int getTotalCertifiedCountByMember(Long memberId);
}
