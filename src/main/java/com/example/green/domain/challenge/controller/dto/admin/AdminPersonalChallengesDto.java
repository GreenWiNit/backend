package com.example.green.domain.challenge.controller.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 개인 챌린지 목록 응답")
public record AdminPersonalChallengesDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-P-20250109-143521-A3FV")
	String challengeCode,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "시작 일시")
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시")
	LocalDateTime endDateTime,

	@Schema(description = "챌린지 포인트", example = "100")
	BigDecimal challengePoint,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplayStatus displayStatus,

	@Schema(description = "생성 일시")
	LocalDateTime createdDate
) {

	private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	public String getPeriod() {
		String startDate = beginDateTime.format(PERIOD_FORMATTER);
		String endDate = endDateTime.format(PERIOD_FORMATTER);

		return startDate + " ~ " + endDate + ".";
	}
}
