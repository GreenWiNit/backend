package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 인증 목록 조회 응답")
public record ChallengeCertificationDto(
	@Schema(description = "챌린지 인증 식별자", example = "1")
	Long id,
	@Schema(description = "챌린지 이름", example = "오늘의 챌린지")
	String challengeName,
	@Schema(description = "챌린지 인증 날짜")
	LocalDate certifiedDate,
	@Schema(description = "챌린지 인증 상태")
	CertificationStatus certificationStatus
) {
}
