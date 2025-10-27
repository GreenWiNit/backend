package com.example.green.domain.challenge.controller.command.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.Challenge;
import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeContent;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeInfo;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AdminChallengeCreateDto(
	@Schema(description = "챌린지명", example = "30일 운동 챌린지", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "챌린지명은 필수값입니다.")
	@Size(max = 90, message = "챌린지명은 90자 이하여야 합니다.")
	String challengeName,

	@Schema(description = "챌린지 포인트", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "챌린지 포인트는 필수값입니다.")
	@Positive(message = "챌린지 포인트는 양수이어야 합니다.")
	BigDecimal challengePoint,

	@Schema(description = "시작 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	LocalDate beginDate,

	@Schema(description = "종료 일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
	LocalDate endDate,

	@Schema(description = "챌린지 설명 및 참여방법", example = "매일 30분 이상 운동하기")
	@NotNull(message = "챌린지 내용은 Null 일 수 없습니다.")
	String challengeContent,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/challenge.jpg")
	@NotBlank(message = "챌린지 이미지 URL은 필수값입니다.")
	String challengeImageUrl,

	@Schema(description = "디스플레이 여부")
	@NotNull
	ChallengeDisplay displayStatus
) {

	@JsonIgnore
	@JsonCreator
	public AdminChallengeCreateDto(
		@JsonProperty("challengeName") String challengeName,
		@JsonProperty("challengePoint") BigDecimal challengePoint,
		@JsonProperty("beginDateTime") LocalDateTime beginDateTime,
		@JsonProperty("endDateTime") LocalDateTime endDateTime,
		@JsonProperty("challengeContent") String challengeContent,
		@JsonProperty("challengeImageUrl") String challengeImageUrl) {

		this(challengeName, challengePoint,
			null,
			null,
			challengeContent, challengeImageUrl,
			ChallengeDisplay.VISIBLE);
	}

	public TeamChallenge toTeamChallenge(String challengeCode) {
		return TeamChallenge.create(
			challengeCode, challengeName, challengeImageUrl, challengeContent,
			challengePoint, null, null, displayStatus
		);
	}

	public PersonalChallenge toPersonalChallenge(String challengeCode) {
		return PersonalChallenge.create(
			challengeCode, challengeName, challengeImageUrl, challengeContent,
			challengePoint, null, null, displayStatus
		);
	}

	public Challenge toChallenge(String challengeCode, ChallengeType challengeType) {
		ChallengeInfo info = ChallengeInfo.of(challengeName, challengePoint.intValue());
		ChallengeContent content = ChallengeContent.of(challengeContent, challengeImageUrl);
		return Challenge.of(challengeCode, info, content, challengeType);
	}
}
