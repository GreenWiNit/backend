package com.example.green.domain.challenge.controller.command.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "어드민 챌린지 수정 요청")
public record AdminChallengeUpdateDto(
	@Schema(description = "챌린지명", example = "30일 운동 챌린지", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "챌린지명은 필수값입니다.")
	@Size(max = 90, message = "챌린지명은 90자 이하여야 합니다.")
	String challengeName,

	@Schema(description = "챌린지 포인트", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "챌린지 포인트는 필수값입니다.")
	@Min(value = 0, message = "챌린지 포인트는 0 이상이어야 합니다.")
	BigDecimal challengePoint,

	@Schema(description = "시작 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "시작 일시는 필수값입니다.")
	LocalDate beginDate,

	@Schema(description = "종료 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "종료 일시는 필수값입니다.")
	LocalDate endDate,

	@Schema(description = "챌린지 설명 및 참여방법", example = "매일 30분 이상 운동하기")
	String challengeContent,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/challenge-image.jpg")
	@NotBlank(message = "챌린지 이미지 URL은 필수값입니다.")
	String challengeImageUrl
) {

	@JsonCreator
	public AdminChallengeUpdateDto(
		@JsonProperty("challengeName") String challengeName,
		@JsonProperty("challengePoint") BigDecimal challengePoint,
		@JsonProperty("beginDateTime") LocalDateTime beginDateTime,
		@JsonProperty("endDateTime") LocalDateTime endDateTime,
		@JsonProperty("challengeContent") String challengeContent,
		@JsonProperty("challengeImageUrl") String challengeImageUrl) {

		this(challengeName, challengePoint,
			beginDateTime.toLocalDate(),
			endDateTime.toLocalDate(),
			challengeContent, challengeImageUrl);
	}
}
