package com.example.green.domain.challenge.controller.dto.admin;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.challenge.BaseChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplayStatus;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 챌린지 상세 응답")
public record AdminChallengeDetailDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-P-20250109-143521-A3FV")
	String challengeCode,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "챌린지 유형")
	ChallengeType challengeType,

	@Schema(description = "챌린지 포인트", example = "100")
	BigDecimal challengePoint,

	@Schema(description = "시작 일시")
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시")
	LocalDateTime endDateTime,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplayStatus displayStatus,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/image.jpg")
	String challengeImage,

	@Schema(description = "챌린지 설명 및 참여방법", example = "매일 30분 이상 운동하기")
	String challengeContent
) {

	public static AdminChallengeDetailDto from(BaseChallenge challenge) {
		return new AdminChallengeDetailDto(
			challenge.getId(),
			challenge.getChallengeCode(),
			challenge.getChallengeName(),
			challenge.getChallengeType(),
			challenge.getChallengePoint(),
			challenge.getBeginDateTime(),
			challenge.getEndDateTime(),
			challenge.getDisplayStatus(),
			challenge.getChallengeImage(),
			challenge.getChallengeContent()
		);
	}
}
