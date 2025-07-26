package com.example.green.domain.challenge.controller.dto.admin;

import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.TeamChallenge;
import com.example.green.domain.challenge.enums.ChallengeDisplayStatus;
import com.example.green.domain.challenge.enums.ChallengeStatus;
import com.example.green.domain.challenge.enums.ChallengeType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "어드민 팀 챌린지 목록 응답")
public record AdminTeamChallengeListResponseDto(
	@Schema(description = "챌린지 ID", example = "1")
	Long id,

	@Schema(description = "챌린지 코드", example = "CH-T-20250109-143522-B7MX")
	String challengeCode,

	@Schema(description = "챌린지명", example = "30일 운동 챌린지")
	String challengeName,

	@Schema(description = "챌린지 상태")
	ChallengeStatus challengeStatus,

	@Schema(description = "챌린지 유형")
	ChallengeType challengeType,

	@Schema(description = "챌린지 포인트", example = "100")
	Integer challengePoint,

	@Schema(description = "시작 일시")
	LocalDateTime beginDateTime,

	@Schema(description = "종료 일시")
	LocalDateTime endDateTime,

	@Schema(description = "전시 상태", example = "VISIBLE")
	ChallengeDisplayStatus displayStatus,

	@Schema(description = "챌린지 이미지 URL", example = "https://example.com/image.jpg")
	String challengeImage,

	@Schema(description = "참여자 수", example = "150")
	Integer participantCount,

	// 팀 챌린지 전용 필드
	@Schema(description = "현재 그룹 수", example = "5")
	Integer currentGroupCount,

	@Schema(description = "최대 그룹 수", example = "10")
	Integer maxGroupCount,

	@Schema(description = "생성 일시")
	LocalDateTime createdDate
) {

	/**
	 * TeamChallenge 엔티티로부터 AdminTeamChallengeListResponseDto를 생성합니다.
	 */
	public static AdminTeamChallengeListResponseDto from(TeamChallenge challenge, Integer participantCount) {
		return new AdminTeamChallengeListResponseDto(
			challenge.getId(),
			challenge.getChallengeCode(),
			challenge.getChallengeName(),
			challenge.getChallengeStatus(),
			challenge.getChallengeType(),
			challenge.getChallengePoint().getAmount().intValue(),
			challenge.getBeginDateTime(),
			challenge.getEndDateTime(),
			challenge.getDisplayStatus(),
			challenge.getChallengeImage(),
			participantCount,
			challenge.getCurrentGroupCount(),
			challenge.getMaxGroupCount(),
			challenge.getCreatedDate()
		);
	}
}
