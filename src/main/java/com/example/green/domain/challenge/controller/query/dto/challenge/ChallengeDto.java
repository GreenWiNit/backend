package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "챌린지 목록 응답")
@Getter
@AllArgsConstructor
public class ChallengeDto {

	@Schema(description = "챌린지 ID", example = "1")
	private final Long id;

	@Schema(description = "챌린지 이름", example = "30일 운동 챌린지")
	private final String challengeName;

	@Schema(description = "시작 일시")
	private final LocalDate beginDate;

	@Schema(description = "종료 일시")
	private final LocalDate endDate;

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/image.jpg")
	private final String challengeImage;

	@Schema(description = "챌린지 포인트", example = "100")
	private final BigDecimal challengePoint;

	@Schema(description = "누적 참여자 수", example = "10")
	private final Integer currentParticipant;

	@JsonIgnore
	private final Long cursor;

	public ChallengeDto(Long id, String challengeName, LocalDate beginDate, LocalDate endDate,
		String challengeImage, BigDecimal challengePoint, Integer currentParticipant) {
		this(id, challengeName, beginDate, endDate, challengeImage, challengePoint, currentParticipant, id);
	}
}