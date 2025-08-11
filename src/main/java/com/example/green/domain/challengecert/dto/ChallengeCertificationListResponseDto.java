package com.example.green.domain.challengecert.dto;

import java.time.LocalDate;

import com.example.green.domain.challenge.entity.certification.CertificationStatus;
import com.example.green.domain.challenge.entity.certification.PersonalChallengeCertification;
import com.example.green.domain.challenge.entity.certification.TeamChallengeCertification;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "챌린지 인증 목록 조회 응답")
@Builder
public record ChallengeCertificationListResponseDto(
	@Schema(description = "인증 ID", example = "123")
	Long id,

	@Schema(description = "회원 ID", example = "1")
	Long memberId,

	@Schema(description = "회원 닉네임", example = "홍길동")
	String memberNickname,

	@Schema(description = "회원 키", example = "google_1234")
	String memberKey,

	@Schema(description = "회원 이메일", example = "test@example.com")
	String memberEmail,

	@Schema(description = "챌린지 ID", example = "1")
	Long challengeId,

	@Schema(description = "챌린지 제목", example = "30일 운동 챌린지")
	String challengeTitle,

	@Schema(description = "챌린지 코드", example = "CH-P-20250109-143521-A3FV")
	String challengeCode,

	@Schema(description = "인증 이미지 URL", example = "https://example.com/image.jpg")
	String certificationImageUrl,

	@Schema(description = "인증 후기", example = "오늘도 열심히 운동했습니다!")
	String certificationReview,

	@Schema(description = "인증 날짜", example = "2024-01-15")
	LocalDate certifiedDate,

	@Schema(description = "인증 상태", example = "REQUESTED")
	CertificationStatus status
) {

	/**
	 * 개인 챌린지 인증 Entity로부터 ListResponseDto를 생성합니다.
	 */
	public static ChallengeCertificationListResponseDto fromPersonalChallengeCertification(
		PersonalChallengeCertification certification) {

		return new ChallengeCertificationListResponseDto(
			certification.getId(),
			certification.getMember().getId(),
			certification.getMember().getProfile().getNickname(),
			certification.getMember().getMemberKey(),
			certification.getMember().getEmail(),
			certification.getParticipation().getPersonalChallenge().getId(),
			certification.getParticipation().getPersonalChallenge().getChallengeName(),
			certification.getParticipation().getPersonalChallenge().getChallengeCode(),
			certification.getCertificationImageUrl(),
			certification.getCertificationReview(),
			certification.getCertifiedDate(),
			certification.getStatus()
		);
	}

	/**
	 * 팀 챌린지 인증 Entity로부터 ListResponseDto를 생성합니다.
	 */
	public static ChallengeCertificationListResponseDto fromTeamChallengeCertification(
		TeamChallengeCertification certification) {

		return new ChallengeCertificationListResponseDto(
			certification.getId(),
			certification.getMember().getId(),
			certification.getMember().getProfile().getNickname(),
			certification.getMember().getMemberKey(),
			certification.getMember().getEmail(),
			certification.getParticipation().getTeamChallenge().getId(),
			certification.getParticipation().getTeamChallenge().getChallengeName(),
			certification.getParticipation().getTeamChallenge().getChallengeCode(),
			certification.getCertificationImageUrl(),
			certification.getCertificationReview(),
			certification.getCertifiedDate(),
			certification.getStatus()
		);
	}
}
