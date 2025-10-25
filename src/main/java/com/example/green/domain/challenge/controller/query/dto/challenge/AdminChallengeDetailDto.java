package com.example.green.domain.challenge.controller.query.dto.challenge;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.green.domain.challenge.entity.challenge.BaseChallenge;
import com.example.green.domain.challenge.entity.challenge.vo.ChallengeDisplay;
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

	@Schema(description = "시작 일자")
	LocalDate beginDate,

	@Schema(description = "종료 일자")
	LocalDate endDate,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplay displayStatus,

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
			challenge.getBeginDate(),
			challenge.getEndDate(),
			challenge.getDisplayStatus(),
			challenge.getChallengeImage(),
			challenge.getChallengeContent()
		);
	}
}
