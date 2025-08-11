package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 챌린지 참가자 목록 응답")
public class AdminChallengeParticipantListResponseDto {
	@Schema(description = "그룹(팀) 코드", example = "T-20250109-143523-C8NQ")
	private String groupCode;

	@Schema(description = "사용자 식별 키", example = "google 12421424")
	private String memberKey;

	@Schema(description = "챌린지 참여 날짜", example = "2025-08-10")
	private LocalDateTime participatingDate;

	@Schema(description = "그룹(팀) 참여 날짜", example = "2025-08-21")
	private LocalDate groupParticipatingDate;

	@JsonIgnore
	private Long memberId;
}
