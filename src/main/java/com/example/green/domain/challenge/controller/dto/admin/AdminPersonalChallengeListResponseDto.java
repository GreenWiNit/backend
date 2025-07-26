package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.PersonalChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 개인 챌린지 목록 응답")
public record AdminPersonalChallengeListResponseDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-P-20250109-143521-A3FV")
	String challengeCode,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "챌린지 포인트", example = "100")
	Integer challengePoint,

	@Schema(description = "시작 일시")
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시")
	LocalDateTime endDateTime,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplayStatus displayStatus,

	@Schema(description = "생성 일시")
	LocalDateTime createdDate
) {

	/**
	 * PersonalChallenge 엔티티로부터 AdminPersonalChallengeListResponseDto를 생성합니다.
	 */
	public static AdminPersonalChallengeListResponseDto from(PersonalChallenge challenge) {
		return new AdminPersonalChallengeListResponseDto(
			challenge.getId(),
			challenge.getChallengeCode(),
			challenge.getChallengeName(),
			challenge.getChallengePoint().getAmount().intValue(),
			challenge.getBeginDateTime(),
			challenge.getEndDateTime(),
			challenge.getDisplayStatus(),
			challenge.getCreatedDate()
		);
	}
}
