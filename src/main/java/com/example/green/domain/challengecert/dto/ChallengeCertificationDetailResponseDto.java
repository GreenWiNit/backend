package com.example.green.domain.challengecert.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;
import com.example.green.domain.challengecert.enums.CertificationStatus;

public record ChallengeCertificationDetailResponseDto(
	Long id,
	Long memberId,
	String memberNickname,
	String memberEmail,
	String certificationImageUrl,
	String certificationReview,
	LocalDateTime certifiedAt,
	LocalDate certifiedDate,
	CertificationStatus status
) {

	public static ChallengeCertificationDetailResponseDto fromPersonalChallengeCertification(
		PersonalChallengeCertification certification) {

		return new ChallengeCertificationDetailResponseDto(
			certification.getId(),
			certification.getMember().getId(),
			certification.getMember().getProfile().getNickname(),
			certification.getMember().getEmail(),
			certification.getCertificationImageUrl(),
			certification.getCertificationReview(),
			certification.getCertifiedAt(),
			certification.getCertifiedDate(),
			certification.getStatus()
		);
	}

	public static ChallengeCertificationDetailResponseDto fromTeamChallengeCertification(
		TeamChallengeCertification certification) {

		return new ChallengeCertificationDetailResponseDto(
			certification.getId(),
			certification.getMember().getId(),
			certification.getMember().getProfile().getNickname(),
			certification.getMember().getEmail(),
			certification.getCertificationImageUrl(),
			certification.getCertificationReview(),
			certification.getCertifiedAt(),
			certification.getCertifiedDate(),
			certification.getStatus()
		);
	}
}
