package com.example.green.domain.certification.ui.dto;

import java.time.LocalDate;

import com.example.green.domain.certification.domain.CertificationStatus;
import com.example.green.domain.certification.domain.ChallengeCertification;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "챌린지 인증 상세 정보")
public record ChallengeCertificationDetailDto(
	@Schema(description = "챌린지 인증 식별자", example = "1")
	Long id,
	@Schema(description = "챌린지 이름", example = "오늘의 챌린지")
	String challengeName,
	@Schema(description = "챌린지 인증 날짜")
	LocalDate certifiedDate,
	@Schema(description = "챌린지 인증 이미지", example = "http://example.com/image.png")
	String imageUrl,
	@Schema(description = "와 최고다")
	String review,
	@Schema(description = "챌린지 인증 상태")
	CertificationStatus certificationStatus
) {
	public static ChallengeCertificationDetailDto from(ChallengeCertification certification) {
		return new ChallengeCertificationDetailDto(
			certification.getId(),
			certification.getChallenge().getChallengeName(),
			certification.getCertifiedDate(),
			certification.getImageUrl(),
			certification.getReview(),
			certification.getStatus()
		);
	}
}
