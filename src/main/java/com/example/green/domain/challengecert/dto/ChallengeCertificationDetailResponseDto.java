package com.example.green.domain.challengecert.dto;

import java.time.LocalDate;

import com.example.green.domain.challenge.enums.ChallengeType;
import com.example.green.domain.challengecert.entity.PersonalChallengeCertification;
import com.example.green.domain.challengecert.entity.TeamChallengeCertification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "챌린지 인증 상세 조회 응답")
@Builder
public record ChallengeCertificationDetailResponseDto(
	@Schema(description = "인증 ID", example = "123")
	Long certificationId,

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "30일 런닝 챌린지")
	String challengeTitle,

	@Schema(description = "챌린지 타입", example = "PERSONAL", allowableValues = {"PERSONAL", "TEAM"})
	String challengeType,

	@Schema(description = "인증 이미지 URL", example = "https://example.com/image.jpg")
	String certificationImageUrl,

	@Schema(description = "인증 후기", example = "오늘도 열심히 운동했습니다!")
	String certificationReview,

	@Schema(description = "인증 날짜", example = "2024-01-15")
	LocalDate certifiedDate,

	@Schema(description = "승인 여부", example = "true")
	Boolean approved
) {
	
	/**
	 * 개인 챌린지 인증 Entity로부터 DetailResponseDto를 생성합니다.
	 */
	public static ChallengeCertificationDetailResponseDto from(PersonalChallengeCertification certification) {
		return ChallengeCertificationDetailResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getPersonalChallenge().getId())
			.challengeTitle(certification.getParticipation().getPersonalChallenge().getChallengeName())
			.challengeType(ChallengeType.PERSONAL.name())
			.certificationImageUrl(certification.getCertificationImageUrl())
			.certificationReview(certification.getCertificationReview())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}

	/**
	 * 팀 챌린지 인증 Entity로부터 DetailResponseDto를 생성합니다.
	 */
	public static ChallengeCertificationDetailResponseDto from(TeamChallengeCertification certification) {
		return ChallengeCertificationDetailResponseDto.builder()
			.certificationId(certification.getId())
			.challengeId(certification.getParticipation().getTeamChallenge().getId())
			.challengeTitle(certification.getParticipation().getTeamChallenge().getChallengeName())
			.challengeType(ChallengeType.TEAM.name())
			.certificationImageUrl(certification.getCertificationImageUrl())
			.certificationReview(certification.getCertificationReview())
			.certifiedDate(certification.getCertifiedDate())
			.approved(certification.getApproved())
			.build();
	}
}
