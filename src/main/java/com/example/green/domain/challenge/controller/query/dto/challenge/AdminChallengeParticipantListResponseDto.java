package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDate;

import com.example.green.domain.challengecert.repository.dao.ChallengeParticipantDao;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 챌린지 참가자 목록 응답")
public record AdminChallengeParticipantListResponseDto(
	@Schema(description = "회원 ID", example = "1")
	Long memberId,

	@Schema(description = "회원 키", example = "google_3421")
	String memberKey,

	@Schema(description = "챌린지 가입 날짜", example = "2025-08-21")
	LocalDate participationDate,

	@Schema(description = "팀 코드(팀 챌린지만)", example = "T-20250109-143523-C8NQ")
	String teamCode,

	@Schema(description = "팀 선택 및 등록 날짜(팀 챌린지만)", example = "2025-08-21")
	LocalDate teamSelectionDate,

	@Schema(description = "인증 횟수(개인 챌린지만)", example = "4")
	Integer certificationCount
) {
	public static AdminChallengeParticipantListResponseDto from(ChallengeParticipantDao dao) {
		return new AdminChallengeParticipantListResponseDto(
			dao.memberId(),
			dao.memberKey(),
			dao.getParticipatedDate(),
			dao.teamCode(),
			dao.getTeamSelectionDate(),
			dao.certificationCount()
		);
	}
}
