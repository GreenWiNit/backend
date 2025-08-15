package com.example.green.domain.challenge.controller.command.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.PersonalChallenge;
import com.example.green.domain.challenge.entity.challenge.TeamChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = """
	어드민 챌린지 생성 요청
	
	필드 설명:
	- challengeName: 챌린지명 (필수, 최대 90자)
	- challengePoint: 챌린지 포인트 (필수, 0 이상)
	- beginDateTime: 시작 일시 (필수, ISO 8601 형식)
	- endDateTime: 종료 일시 (필수, ISO 8601 형식)  
	- challengeContent: 챌린지 설명 및 참여방법
	- challengeImageUrl: 챌린지 이미지 URL
	- displayStatus: 디스플레이 여부 (HIDDEN=숨김, VISIBLE=표시)
	""")
public record AdminChallengeCreateDto(
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
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "종료 일시는 필수값입니다.")
	LocalDateTime endDateTime,

	@Schema(description = "챌린지 설명 및 참여방법", example = "매일 30분 이상 운동하기")
	String challengeContent,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/challenge.jpg")
	String challengeImageUrl,

	@Schema(description = "디스플레이 여부")
	ChallengeDisplayStatus displayStatus
) {

	public TeamChallenge toTeamChallenge(String challengeCode) {
		return TeamChallenge.create(
			challengeCode, challengeName, challengeImageUrl, challengeContent,
			challengePoint, toBeginDate(), toEndDate(), displayStatus
		);
	}

	public PersonalChallenge toPersonalChallenge(String challengeCode) {
		return PersonalChallenge.create(
			challengeCode, challengeName, challengeImageUrl, challengeContent,
			challengePoint, toBeginDate(), toEndDate(), displayStatus
		);
	}

	public LocalDate toBeginDate() {
		return beginDateTime.toLocalDate();
	}

	public LocalDate toEndDate() {
		return endDateTime.toLocalDate();
	}
}
