package com.example.green.domain.challenge.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.green.domain.challenge.entity.BaseChallenge;

import lombok.Builder;
import lombok.Getter;

/**
 * 챌린지 상세 응답 DTO
 */
@Getter
public class ChallengeDetailResponseDto {
	private final Long id;
	private final String title;
	private final LocalDateTime beginDateTime;
	private final LocalDateTime endDateTime;
	private final String imageUrl;
	private final BigDecimal point;
	private final ChallengeParticipationStatus participationStatus;

	@Builder
	public ChallengeDetailResponseDto(Long id, String title, LocalDateTime startDate, LocalDateTime endDate,
		String imageUrl, BigDecimal point, ChallengeParticipationStatus participationStatus) {
		this.id = id;
		this.title = title;
		this.beginDateTime = startDate;
		this.endDateTime = endDate;
		this.imageUrl = imageUrl;
		this.point = point;
		this.participationStatus = participationStatus;
	}

	public static ChallengeDetailResponseDto from(BaseChallenge challenge, ChallengeParticipationStatus status) {
		return ChallengeDetailResponseDto.builder()
			.id(challenge.getId())
			.title(challenge.getChallengeName())
			.startDate(challenge.getBeginDateTime())
			.endDate(challenge.getEndDateTime())
			.imageUrl(challenge.getChallengeImage())
			.point(challenge.getChallengePoint().getAmount())
			.participationStatus(status)
			.build();
	}
}
