package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 목록 응답")
public record AdminTeamChallengesDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-T-20250109-143522-B7MX")
	String challengeCode,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "시작 일시")
	LocalDate beginDate,

	@Schema(description = "종료 일시")
	LocalDate endDate,

	@Schema(description = "챌린지 포인트", example = "100")
	BigDecimal challengePoint,

	@Schema(description = "팀 수", example = "4")
	Long teamCount,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplay displayStatus,

	@Schema(description = "생성 일시")
	LocalDateTime createdDate
) {
	private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	public static AdminTeamChallengesDto of(AdminChallengesDto it) {
		return new AdminTeamChallengesDto(
			it.getId(),
			it.getCode(),
			it.getName(),
			null,
			null,
			BigDecimal.valueOf(it.getPoint()),
			it.getTeamCount(),
			it.getDisplay(),
			it.getCreatedDate()
		);
	}

	public String getPeriod() {
		return beginDate.format(PERIOD_FORMATTER) + " ~ " + endDate.format(PERIOD_FORMATTER) + ".";
	}
}
