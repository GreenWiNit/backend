package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;

public record ChallengeCertificationDto(
	Long id,
	String challengeName,
	LocalDate certifiedDate,
	CertificationStatus certificationStatus
) {
}
