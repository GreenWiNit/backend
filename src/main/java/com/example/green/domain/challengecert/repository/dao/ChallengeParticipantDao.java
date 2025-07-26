package com.example.green.domain.challengecert.repository.dao;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 챌린지 참여자 정보를 담는 DAO
 */
public record ChallengeParticipantDao(
	Long memberId,
	String memberKey,
	LocalDateTime participatedAt,
	String teamCode,            // TeamChallenge 참여자의 경우에만 값 존재
	LocalDateTime teamSelectionDateTime, // TeamChallenge 참여자의 경우에만 값 존재
	Integer certificationCount  // 인증 횟수 (향후 구현시 사용)
) {
	public LocalDate getParticipatedDate() {
		return participatedAt != null ? participatedAt.toLocalDate() : null;
	}

	public LocalDate getTeamSelectionDate() {
		return teamSelectionDateTime != null ? teamSelectionDateTime.toLocalDate() : null;
	}
}
