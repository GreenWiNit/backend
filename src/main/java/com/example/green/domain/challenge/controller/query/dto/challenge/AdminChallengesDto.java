package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 목록 응답")
public record AdminChallengesDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-T-20250109-143522-B7MX")
	String code,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String name,

	@Schema(description = "챌린지 포인트", example = "100")
	Integer point,

	@Schema(description = "팀 수", example = "4")
	Integer teamCount,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplay display,

	@Schema(description = "생성 일시")
	LocalDateTime createdDate
) {
}
