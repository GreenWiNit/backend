package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "어드민 챌린지 생성 요청")
public record AdminChallengeCreateRequestDto(
	@Schema(description = "챌린지명", example = "30일 운동 챌린지", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "챌린지명은 필수값입니다.")
	@Size(max = 90, message = "챌린지명은 90자 이하여야 합니다.")
	String challengeName,

	@Schema(description = "챌린지 포인트", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "챌린지 포인트는 필수값입니다.")
	@Min(value = 0, message = "챌린지 포인트는 0 이상이어야 합니다.")
	Integer challengePoint,

	@Schema(description = "챌린지 유형 (PERSONAL: 개인 챌린지, TEAM: 팀 챌린지)",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "PERSONAL")
	@NotNull(message = "챌린지 유형은 필수값입니다.")
	ChallengeType challengeType,

	@Schema(description = "시작 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "시작 일시는 필수값입니다.")
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시", requiredMode = Schema.RequiredMode.REQUIRED)
	@NotNull(message = "종료 일시는 필수값입니다.")
	LocalDateTime endDateTime,

	@Schema(description = "전시 상태 (VISIBLE: 사용자에게 보임, HIDDEN: 사용자에게 보이지 않음)",
		requiredMode = Schema.RequiredMode.REQUIRED,
		example = "VISIBLE")
	@NotNull(message = "전시 상태는 필수값입니다.")
	ChallengeDisplayStatus displayStatus,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/challenge.jpg")
	String challengeImageUrl,

	@Schema(description = "챌린지 설명 및 참여방법", example = "매일 30분 이상 운동하기")
	String challengeContent,

	// 팀 챌린지 전용 필드
	@Schema(description = "최대 그룹 수 (팀 챌린지만)", example = "10")
	@Min(value = 1, message = "최대 그룹 수는 1 이상이어야 합니다.")
	Integer maxGroupCount
) {
}
